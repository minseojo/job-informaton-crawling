public class Main {
    // 드라이버 설치 경로
    private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    private static final String WEB_DRIVER_PATH = "/Users/minseojo/Desktop/chromedriver-mac-arm64/chromedriver";
    public static void main(String[] args) {
        JobInformationCrawler crawler = new JobInformationCrawler(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        crawler.exe();
    }
}
