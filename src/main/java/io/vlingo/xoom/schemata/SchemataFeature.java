package io.vlingo.xoom.schemata;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.util.VMError;
@AutomaticFeature
public final class SchemataFeature implements Feature {

    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        return access.findClassByName("io.vlingo.xoom.schemata") != null;
    }

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        try {
            RuntimeReflection.register(java.lang.Object[].class);
        } catch (Exception ex) {
            throw VMError.shouldNotReachHere(ex);
        }
    }
}