package com.cosmo.cats.api.featuretoggle;


import com.cosmo.cats.api.config.FeatureToggleProperties;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

  private final ConcurrentHashMap<String, Boolean> featureToggles;

  public FeatureToggleService(FeatureToggleProperties featureToggleProperties) {
    featureToggles = new ConcurrentHashMap<>(featureToggleProperties.getToggles());
  }

  public boolean check(String featureName) {
    return featureToggles.getOrDefault(featureName, false);
  }

  public void enable(String featureName) {
    featureToggles.put(featureName, true);
  }

  public void disable(String featureName) {
    featureToggles.put(featureName, false);
  }
}
