package cwru.mjq;



import java.util.Map;
import java.util.Set;

public class HtmlEntity {

    private Map<String, String> headerMap;

    private String url;
    private String content;
    private String body;
    private String title;

    private Map<String, String> metas;

    private Set<String> linksSet;

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getLinksSet() {
        return linksSet;
    }

    public void setLinksSet(Set<String> linksSet) {
        this.linksSet = linksSet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getMetas() {
        return metas;
    }

    public void setMetas(Map<String, String> metas) {
        this.metas = metas;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HtmlEntity [headerMap=" + headerMap + ", url=" + url
                + ", content=" + content + ", tittle=" + title + ", metas="
                + metas + ", linksSet=" + linksSet + "]";
    }

}
