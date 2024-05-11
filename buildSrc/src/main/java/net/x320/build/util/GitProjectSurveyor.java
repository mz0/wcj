package net.x320.build.util;

import net.x320.build.GitProjectInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.x320.build.SemVer.semVer;

public class GitProjectSurveyor {

    public GitProjectInfo survey(Path gitPath) throws IOException {
        String currBranch;
        String baseTag;
        String fullVersion;
        Status treeStatus;
        try (Repository repo = new FileRepository(gitPath.toFile());
             var reader = repo.newObjectReader();
             var walk = new RevWalk(reader)
        ) {
            currBranch = repo.getBranch();
            if (currBranch == null) {
                throw new IOException(String.format("%s is unlikely a Git repo", gitPath));
            }
            walk.setRetainBody(false); // we don't need commit bodies
            var headObjectId = repo.getRefDatabase().findRef("HEAD").getObjectId();
            if (headObjectId == null) {
                throw new IOException("No HEAD commit. Presuming repo is empty.");
            }
            treeStatus = getStatus(repo);
            var semVerOptional = getLatest(getTags(repo, walk, true, semVer));
            if (semVerOptional.isEmpty()) {
                throw new IllegalStateException(String.format("no semVer tags matching \"%s\" found ", semVer));
            }
            var latestTag = semVerOptional.get();
            var headCommit = walk.parseCommit(headObjectId);
            var rcl = revList(walk, headCommit, latestTag.getValue().toObjectId());
            var buildSuffix = !rcl.isEmpty() ? "+" + rcl.size() : "";
            baseTag = latestTag.getKey();
            fullVersion = baseTag + buildSuffix;
        } catch (GitAPIException ex) {
            throw new IOException(String.format("Error reading Git status in %s", gitPath), ex);
        }
        return new GitProjectInfo(currBranch, baseTag, fullVersion, treeStatus);
    }

    private static List<RevCommit> revList(RevWalk walk, RevCommit start, ObjectId end) throws IOException {
        walk.reset();
        walk.setRevFilter(RevFilter.ALL);
        walk.markStart(start);
        ArrayList<RevCommit> commits = new ArrayList<>();
        for (RevCommit commit : walk) {
            if (commit.toObjectId().equals(end)) break;
            commits.add(commit);
        }
        return commits;
    }

    private static Optional<Map.Entry<String, RevCommit>> getLatest(Map<String, RevCommit> semVerTags) {
        return semVerTags.entrySet().stream()
                .max(Comparator.comparing((Map.Entry<String, RevCommit> me) -> ordinal(me.getKey())));
    }

    private static int ordinal(String semVerTag) {
        int abc = 0;
        Pattern pattern = Pattern.compile(semVer);
        java.util.regex.Matcher matcher = pattern.matcher(semVerTag);
        if (matcher.matches()) {
            int a = Integer.parseInt(matcher.group(1));
            int b = Integer.parseInt(matcher.group(2));
            int c = Integer.parseInt(matcher.group(3));
            if (c < 1000 && b < 7483 && a < 215) { // maxInt 214_7483_647 > 214_1231_999
                abc = a * 10_000 * 1000 + b * 1000 + c;
            }
        }
        return abc;
    }

    private static Map<String, RevCommit> getTags(Repository repo, RevWalk walk, boolean onlyAnnotated, String match)
            throws IOException
    {
        var result = new HashMap<String, RevCommit>();
        for (var ref : repo.getRefDatabase().getRefsByPrefix(Constants.R_TAGS)) {
            var tag = repo.getRefDatabase().peel(ref);
            // only annotated tags return a peeled object id
            var isLightweight = tag.getPeeledObjectId() == null;
            var objectId =  !onlyAnnotated && isLightweight ? tag.getObjectId() : tag.getPeeledObjectId();
            if (objectId != null) {
                var commit = walk.parseCommit(objectId);
                var tagName = Repository.shortenRefName(ref.getName());
                if (tagName.matches(match)) {
                    result.put(tagName, commit);
                }
            }
        }
        return result;
    }

    private static Status getStatus(Repository repo) throws GitAPIException {
        return new Git(repo).status().call();
    }
}
