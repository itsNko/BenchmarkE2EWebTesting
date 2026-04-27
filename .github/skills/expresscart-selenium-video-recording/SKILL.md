# Selenium per-test Video Recording Implementation

## Overview
This skill provides a reusable pattern to implement per-test E2E video recording with Selenium Remote WebDriver, Docker Compose, and JUnit. Videos are automatically named after test methods and exported to a local `videos/` directory.

## Prerequisites
- Maven-based Java test project
- Selenium WebDriver (Remote)
- JUnit 4
- Docker and Docker Compose
- Selenium Standalone Chrome image with video support

## Implementation Steps

### 1. Create docker-compose.yml

Place this in your baseline test directory (e.g., `expresscart/baseline/expresscart-1.19/`):

```yaml
version: '3.8'

services:
  expresscart:
    image: olianasd/expresscart-strongpsw
    container_name: expresscart
    ports:
      - "3000:1111"
    stdin_open: true
    tty: true
    networks:
      - test-network

  maven-java21:
    image: maven:3.9-eclipse-temurin-21
    container_name: expresscart-maven
    working_dir: /app
    volumes:
      - .:/app
      - maven-repo:/root/.m2
    command: tail -f /dev/null
    stdin_open: true
    tty: true
    networks:
      - test-network
    depends_on:
      - browser
      - expresscart

  browser:
    image: selenium/standalone-chrome:latest
    container_name: browser
    ports:
      - "4444:4444"
      - "7900:7900"
    shm_size: 2gb
    volumes:
      - /dev/shm:/dev/shm
    environment:
      - SE_SCREEN_WIDTH=1920
      - SE_SCREEN_HEIGHT=1080
      - SE_START_XVFB=true
      - SE_START_VNC=true
      - SE_START_NO_VNC=true
    networks:
      - test-network

  chrome-video:
    image: selenium/video:latest
    container_name: chrome-video
    depends_on:
      - browser
    volumes:
      - ./videos:/videos
    environment:
      - DISPLAY_CONTAINER_NAME=browser
      - SE_VIDEO_FILE_NAME=auto
      - SE_VIDEO_FILE_NAME_SUFFIX=false
      - SE_VIDEO_RECORD_STANDALONE=true
      - SE_NODE_GRID_URL=http://browser:4444
      - SE_SCREEN_WIDTH=1920
      - SE_SCREEN_HEIGHT=1080
    networks:
      - test-network

networks:
  test-network:
    driver: bridge

volumes:
  maven-repo:
```

**Key points:**
- `browser` service: Standalone Chrome with XVFB, VNC, and NoVNC enabled
- `chrome-video` service: Records videos independently and names them automatically
- `SE_VIDEO_FILE_NAME=auto`: Automatically names videos based on test session name
- `SE_VIDEO_RECORD_STANDALONE=true`: Records without depending on test status
- All services share the `test-network` bridge

### 2. Update BaseTest.java

Add JUnit hooks and Selenium capabilities for video recording:

```java
@Before
public void setUp() {
    System.out.println("\n========== TEST START: " + testName.getMethodName() + " ==========");
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--no-sandbox", "--disable-gpu", "--screen-info={1920x1080}");
    
    // Enable video recording with Selenium Grid
    chromeOptions.setCapability("se:recordVideo", true);
    chromeOptions.setCapability("se:timeZone", "UTC");
    chromeOptions.setCapability("se:screenResolution", "1920x1080");
    
    // Critical: Set test name for auto-naming videos
    chromeOptions.setCapability("se:name", testName.getMethodName());
    chromeOptions.setCapability("se:videoUploadOnPass", true);
    
    try {
        driver = new RemoteWebDriver(new URL("http://browser:4444/wd/hub"), chromeOptions);
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    driver.manage().window().maximize();
    driver.get(app_url);
}

@After
public void tearDown() {
    String testMethodName = testName.getMethodName();
    System.out.println("========== TEST FINISH: " + testMethodName + " ==========\n");
    driver.quit();
    // The selenium/video service automatically records and names the video
    // No manual renaming needed
}
```

**Key points:**
- `se:recordVideo` capability enables video recording
- **`se:name`**: CRITICAL—must match test method name for proper video naming
- **`se:videoUploadOnPass`**: Records video even for passing tests
- **No manual renaming in `@After`**: The `chrome-video` service handles naming automatically using the session name
- Video is named as `{se:name}.mp4` (e.g., `testAddDiscountCodeAmount.mp4`)

### 3. Create the videos/ directory

Ensure a `videos/` directory exists in your baseline (should be `.gitkeep`):

```bash
mkdir -p videos
touch videos/.gitkeep
```

### 4. Running Tests with Docker Compose

From your baseline directory (e.g., `expresscart/baseline/expresscart-1.19/`):

```bash
# Start services
docker compose up -d

# Run tests (example)
docker compose exec -T maven-java21 mvn -Dtest=TestSuite test

# Or run a single test class
docker compose exec -T maven-java21 mvn -Dtest=YourTestClass test

# Stop services
docker compose down
```

**Output:**
- Videos are written to `./videos/{testMethodName}.mp4`
- Test output indicates video recording status

### 5. Jenkins/CI Integration (Optional)

To persist videos as artifacts in CI/CD:

```groovy
post {
    always {
        junit 'baseline/*/target/surefire-reports/**/*.xml'
        archiveArtifacts artifacts: 'baseline/*/videos/**/*.mp4', allowEmptyArchive: true
    }
}
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Videos directory does not exist" | Ensure `mkdir -p videos` was run; check `chrome-video` container is running with `docker ps` |
| No .mp4 files found | Check `docker logs chrome-video` for video encoding errors; verify `SE_VIDEO_RECORD_STANDALONE=true` |
| Files found but incorrect naming | Ensure `se:name` capability is set to `testName.getMethodName()` in BaseTest |
| Test videos not appearing | Verify `chrome-video` service has `depends_on: browser` and shares the same network |
| Volume mount permission errors | Ensure `./videos:/videos` and directory is writable on host |
| Test method names don't match videos | Ensure test method names are unique and match their intended feature names |

## References

- [Selenium Grid Video Recording](https://www.selenium.dev/documentation/grid/configuration/distributed_components/)
- [Docker Compose Networking](https://docs.docker.com/compose/networking/)
- [JUnit Rules and Hooks](https://junit.org/junit4/javadoc/latest/org/junit/Rule.html)