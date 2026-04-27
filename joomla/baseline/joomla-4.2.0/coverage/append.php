<?php
// Auto-appended on every HTTP request to stop and dump coverage.
require_once '/coverage-tools/vendor/autoload.php';

if (isset($GLOBALS['E2E_COVERAGE'])) {
    $coverage = $GLOBALS['E2E_COVERAGE'];
    $coverage->stop();

    $dumpDir = '/app/coverage/dumps';
    if (!is_dir($dumpDir)) {
        mkdir($dumpDir, 0777, true);
    }

    $writer = new \SebastianBergmann\CodeCoverage\Report\PHP();
    $filename = $dumpDir . '/request-' . microtime(true) . '-' . uniqid() . '.cov';
    $writer->process($coverage, $filename);
}
