package net.ssehub.kernel_haven.variability_model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.provider.AbstractProvider;

/**
 * The provider for the variability model. This class serves as an intermediate between the analysis and the
 * variability model extractor.
 *
 * @author Adam
 */
public class VariabilityModelProvider extends AbstractProvider<VariabilityModel> {

    @Override
    protected long getTimeout() {
        return config.getValue(DefaultSettings.VARIABILITY_PROVIDER_TIMEOUT);
    }
    
    @Override
    protected List<File> getTargets() {
        List<File> result = new ArrayList<>(1);
        result.add(config.getValue(DefaultSettings.SOURCE_TREE));
        return result;
    }

    @Override
    protected AbstractCache<VariabilityModel> createCache() {
        return new VariabilityModelCache(config.getValue(DefaultSettings.CACHE_DIR));
    }

    @Override
    public boolean readCache() {
        return config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_READ);
    }

    @Override
    public boolean writeCache() {
        return config.getValue(DefaultSettings.VARIABILITY_PROVIDER_CACHE_WRITE);
    }

    @Override
    public int getNumberOfThreads() {
        return 1;
    }

}