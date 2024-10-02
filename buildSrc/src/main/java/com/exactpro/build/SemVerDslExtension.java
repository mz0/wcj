package com.exactpro.build;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

public class SemVerDslExtension {
    public static final String NAME = "semver";

    final Property<String> fallbackVersion;

    public Property<String> getFallbackVersion() {
        return fallbackVersion;
    }

    @SuppressWarnings("unused")  // Bean convention
    public void setFallbackVersion(String fallbackVersion) {
        this.fallbackVersion.set(fallbackVersion);
    }

    @Inject
    public SemVerDslExtension(ObjectFactory objectFactory) {
        this.fallbackVersion = objectFactory.property(String.class);
    }
}
