package crawler.parallel;

import crawler.parallel.filter.Filter;
import crawler.parallel.crawler.Crawler;
import crawler.parallel.generator.QueryGenerator;
import crawler.parallel.crawler.SetupCrawler;
import crawler.parallel.filter.Careers;
import crawler.parallel.filter.Jobs;
import crawler.parallel.vo.Resolution;
import crawler.parallel.filter.Salaries;

import java.util.LinkedHashMap;
import java.util.Map;

import static crawler.parallel.filter.Filter.*;

public class Controller {
    private final Input input;

    public Controller(Input input) {
        this.input = input;
    }

    public void run() {
        Map<String, Filter> appendFilters = prepareAppendFilter(); // 필터 크롤링
        Map<String, String> appendQuery = prepareAppendQueries(appendFilters); // 크롤링한 필터에 대한 쿼리 생성
        crawlRecruitment(appendQuery); // 필터를 적용한 쿼리를 가지고, 프로그래머스 채용 정보 크롤링 시작
    }

    private Map<String, Filter> prepareAppendFilter() {
        SetupCrawler setupCrawler = new SetupCrawler();

        // (직무, 경력, 연봉) 옵션 크롤링
        Map<Integer, String> crawlJobs = setupCrawler.crawlJobs();
        Map<Integer, String> crawlCareers = setupCrawler.crawlCareers();
        Map<Integer, String> crawlSalaries = setupCrawler.crawlSalary();

        // 크롤링한 정보를 메모리에 저장
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("Jobs", new Jobs(crawlJobs));
        filters.put("Careers", new Careers(crawlCareers));
        filters.put("Salaries", new Salaries(crawlSalaries));

        return filters;
    }

    private Map<String, String> prepareAppendQueries(Map<String, Filter> filters) {
        Map<String, String> queries = new LinkedHashMap<>();

        // 희망 (직무, 경력, 연봉) 입력 받기 & 쿼리 생성
        QueryGenerator queryGenerator = new QueryGenerator(filters);

        readFilterJobs(filters, queries, queryGenerator);
        readFilterCareer(filters, queries, queryGenerator);
        readFilterSalary(filters, queries, queryGenerator);

        return queries;
    }

    private void crawlRecruitment(Map<String, String> queries) {
        // 모니터 해상도 입력 받아오기
        Resolution resolution = readResolution();

        // 뒤에 추가 할 쿼리 생성
        String appendQuery = queries.get(JOBS) + queries.get(CAREERS) + queries.get(SALARIES);

        // 스레드 생성, 사용자 컴퓨터 CPU 코어 개수, m1 air 기준 8개;
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("사용자 컴퓨터 CPU 코어 개수 : " + numberOfThreads);

        // 크롤러 생성
        Crawler crawler = new Crawler(resolution, appendQuery, numberOfThreads);
        crawler.execute();
    }

    private void readFilterJobs(Map<String, Filter> filters, Map<String, String> queries, QueryGenerator queryGenerator) {
        while (true) {
            try {
                String inputJobs = input.readJobs(filters.get(JOBS).getFilters());
                String jobsQuery = queryGenerator.generateJobsQuery(inputJobs, (Jobs) filters.get(JOBS));
                queries.put(JOBS, jobsQuery);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void readFilterCareer(Map<String, Filter> filters, Map<String, String> queries, QueryGenerator queryGenerator) {
        while (true) {
            try {
                String inputCareer = input.readCareers(filters.get(CAREERS).getFilters());
                String careersQuery  = queryGenerator.generateCareerQuery(inputCareer, (Careers) filters.get(CAREERS));
                queries.put(CAREERS, careersQuery);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void readFilterSalary(Map<String, Filter> filters, Map<String, String> queries, QueryGenerator queryGenerator) {
        while (true) {
            try {
                String inputSalary = input.readSalary(filters.get(SALARIES).getFilters());
                String salaryQuery  = queryGenerator.generateSalaryQuery(inputSalary, (Salaries) filters.get(SALARIES));
                queries.put(SALARIES, salaryQuery);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Resolution readResolution() {
        while (true) {
            try {
                String[] inputResolution = input.readResolution().split("\\*"); // 1920*1080 을 두개로 나눔, [1] = width, [2] = height
                return new Resolution(inputResolution[0], inputResolution[1]);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
