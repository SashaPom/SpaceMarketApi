package com.cosmo.cats.api.featuretoggle;

import com.cosmo.cats.api.annotation.DisableFeatureToggle;
import com.cosmo.cats.api.annotation.EnableFeatureToggle;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class FeatureToggleExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    context.getTestMethod().ifPresent(method -> {
      FeatureToggleService featureToggleService = getFeatureToggleService(context);
      if (method.isAnnotationPresent(EnableFeatureToggle.class)) {
        EnableFeatureToggle enableFeatureToggle = method.getAnnotation(EnableFeatureToggle.class);
        featureToggleService.enable(enableFeatureToggle.value().getWearerName());
      } else if (method.isAnnotationPresent(DisableFeatureToggle.class)) {
        DisableFeatureToggle disableFeatureToggle = method.getAnnotation(DisableFeatureToggle.class);
        featureToggleService.disable(disableFeatureToggle.value().getWearerName());
      }
    });
  }

  private FeatureToggleService getFeatureToggleService(ExtensionContext context) {
    return SpringExtension.getApplicationContext(context).getBean(FeatureToggleService.class);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    context.getTestMethod().ifPresent(method -> {
      String featureName = null;

      if (method.isAnnotationPresent(EnableFeatureToggle.class)) {
        EnableFeatureToggle enabledFeatureToggleAnnotation = method.getAnnotation(EnableFeatureToggle.class);
        featureName = enabledFeatureToggleAnnotation.value().getWearerName();
      } else if (method.isAnnotationPresent(DisableFeatureToggle.class)) {
        DisableFeatureToggle disabledFeatureToggleAnnotation = method.getAnnotation(DisableFeatureToggle.class);
        featureName = disabledFeatureToggleAnnotation.value().getWearerName();
      }
      if (featureName != null) {
        FeatureToggleService featureToggleService = getFeatureToggleService(context);
        if (getFeatureNamePropertyAsBoolean(context, featureName)) {
          featureToggleService.enable(featureName);
        } else {
          featureToggleService.disable(featureName);
        }
      }
    });
  }

  private boolean getFeatureNamePropertyAsBoolean(ExtensionContext context, String featureName) {
    Environment environment = SpringExtension.getApplicationContext(context).getEnvironment();
    return environment.getProperty("application.feature.toggles." + featureName, Boolean.class, Boolean.FALSE);
  }
}
