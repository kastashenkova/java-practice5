package org.example;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Offline operations test suite")
@SelectClasses(ClusterPruningTest.class)
@IncludeTags("offline")
public class OfflineTestSuite {
}
