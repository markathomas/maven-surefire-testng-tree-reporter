package org.apache.maven.plugin.surefire.extensions.testng;

import org.apache.maven.plugin.surefire.extensions.SurefireStatelessTestsetInfoReporter;
import org.apache.maven.plugin.surefire.report.Theme;

/**
 * Extension of {@link SurefireStatelessTestsetInfoReporter file and console reporter of test-set} for TestNG.
 *
 * @deprecated Use {@link TestNGStatelessTestsetInfoTreeReporter} and set the parameter {@code theme} to {@code UNICODE}.
 * @author <a href="mailto:fabriciorby@hotmail.com">Fabrício Yamamoto (fabriciorby)</a>
 */
@Deprecated
public class TestNGStatelessTestsetInfoTreeReporterUnicode extends TestNGStatelessTestsetInfoTreeReporter {
    @Override
    public Theme getTheme() {
        return Theme.UNICODE;
    }
}
