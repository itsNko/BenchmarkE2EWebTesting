<?php
// Auto-prepended on every HTTP request to start coverage.
require_once '/coverage-tools/vendor/autoload.php';

use SebastianBergmann\CodeCoverage\CodeCoverage;
use SebastianBergmann\CodeCoverage\Driver\Selector;
use SebastianBergmann\CodeCoverage\Filter;

function coverage_build_file_list(array $includeDirs): array
{
    $files = [];
    $excludeDirs = [
        '/app/coverage',
        '/app/cache',
        '/app/tmp',
        '/app/administrator/logs',
        '/var/www/phpmyadmin',
    ];

    foreach ($includeDirs as $dir) {
        if (!is_dir($dir)) {
            continue;
        }

        $iterator = new RecursiveIteratorIterator(
            new RecursiveDirectoryIterator($dir, FilesystemIterator::SKIP_DOTS)
        );

        foreach ($iterator as $fileInfo) {
            if (!$fileInfo->isFile() || substr($fileInfo->getPathname(), -4) !== '.php') {
                continue;
            }

            $path = $fileInfo->getPathname();
            $skip = false;
            foreach ($excludeDirs as $exclude) {
                if (strpos($path, $exclude) === 0) {
                    $skip = true;
                    break;
                }
            }
            if (!$skip) {
                $files[] = $path;
            }
        }
    }

    sort($files);
    return $files;
}

$filter = new Filter();
$listFile = '/app/coverage/file-list.json';

// Only include selected Joomla application code, not vendor/cache/tmp
$includeDirs = [
    '/app/libraries',
    '/app/components',
    '/app/modules',
    '/app/administrator/components',
];

if (is_file($listFile)) {
    $decoded = json_decode(file_get_contents($listFile), true);
    $files = is_array($decoded) ? $decoded : [];
} else {
    $files = coverage_build_file_list($includeDirs);
	file_put_contents($listFile, json_encode($files));
}

if (!empty($files)) {
	$filter->includeFiles($files);
}

// Prefer branch/path coverage when supported, otherwise fall back to line coverage.
$selector = new Selector();
if (method_exists($selector, 'forLineAndBranchCoverage')) {
    $driver = $selector->forLineAndBranchCoverage($filter);
} elseif (method_exists($selector, 'forLineAndPathCoverage')) {
    $driver = $selector->forLineAndPathCoverage($filter);
} else {
    $driver = $selector->forLineCoverage($filter);
}
$coverage = new CodeCoverage($driver, $filter);

$coverageId = $_SERVER['REQUEST_URI'] ?? 'e2e-request';
$coverage->start($coverageId);

$GLOBALS['E2E_COVERAGE'] = $coverage;
