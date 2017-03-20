import com.google.gson.Gson;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    private static final Map<String, String> filter = new HashMap<>();

    public static void main(String[] args) throws IOException {
        final GitHubClient client = new GitHubClient();
        client.setOAuth2Token("1db8cc79b8bb0299ccb39441d24294c8945ab6a6"); //bot token
        final IssueService service = new IssueService(client);
        final RepositoryService repositoryService = new RepositoryService(client);
//        final Repository k9 = repositoryService.getRepositories().get(0);
        final Repository k9 = repositoryService.getRepository("PowerOfThree", "k-9");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        filter.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN);

	port(80);

        get("/", (new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "It works!";
            }
        }));

        post("/create_issue/", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                String reportBody = request.body();
                System.out.println("Report body:" + reportBody);
                Gson gson = new Gson();
                Report report = gson.fromJson(reportBody, Report.class);
                System.out.println(report);
                if (issueExists(report.getStacktrace(), service, k9)) {
                    return "Issue already exists.";
                }

                Issue issue = new Issue();
                issue.setTitle(report.getMessage())
                        .setBody(String.format(
                                "In thread \"%s\":%n" +
                                        "Stack trace:%n%s%n" +
                                        "Created at: %s",
                                report.getThread(),
                                report.getStacktrace(),
                                dateFormat.format((new Date(report.getDate())))));

                service.createIssue(k9, issue);

                return "Issue has been created.";
            }
        });
    }

    private static boolean issueExists(String issueBody, IssueService issueService, Repository repository) {
        try {
            for (Issue issue : issueService.getIssues(repository, filter)) {
                if (issue.getBody().contains(issueBody)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while checking if an issue exists.");
        }
        return false;
    }
}
