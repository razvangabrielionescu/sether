package org.mware.sponge.browser.util;

import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class PageDownloader {
    private static final Logger log = LoggerFactory.getLogger(PageDownloader.class);

    private HttpClient httpClient;

    public PageDownloader(String userAgent) {
        HttpCrawlerConfig config = new HttpCrawlerConfig();
        httpClient = config.getHttpClientFactory().createHTTPClient(userAgent);
    }

    public String downloadPage(String url, int timeout) {
        HttpGet method = null;

        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .build();

            method = new HttpGet(url);
            method.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                String contentType =
                        response.getFirstHeader("Content-Type").getValue();
                if ("application/x-gzip".equals(contentType)
                        || "application/gzip".equals(contentType)) {
                    is = new GZIPInputStream(is);
                }

                String pageContent = IOUtils.toString(is, "UTF-8");
                IOUtils.closeQuietly(is);

                return pageContent;
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                log.debug("Cannot download page: "+url+". HTTP Statuc code is "+statusCode);
            }
        } catch (Exception ex) {
            log.error("Cannot download page: "+url, ex);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

        return null;
    }

    public void closeHttpClient() {
        if (httpClient instanceof CloseableHttpClient) {
            try {
                ((CloseableHttpClient) httpClient).close();
            } catch (IOException e) {
                log.error("Cannot close HttpClient.", e);
            }
        }
    }
}
