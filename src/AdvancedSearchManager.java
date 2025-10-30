import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AdvancedSearchManager {
    // ... existing code ...

    // Fix the searchByMetadata method to have proper implementation
    public static List<SearchResult> searchByMetadata(String filename, String extension, Date startDate, Date endDate) {
        List<SearchResult> results = new ArrayList<>();

        try (IndexReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

            // Add filename filter
            if (filename != null && !filename.trim().isEmpty()) {
                Query filenameQuery = new WildcardQuery(new Term("filename", "*" + filename + "*"));
                booleanQuery.add(filenameQuery, BooleanClause.Occur.MUST);
            }

            // Add extension filter
            if (extension != null && !extension.trim().isEmpty()) {
                Query extensionQuery = new TermQuery(new Term("extension", extension));
                booleanQuery.add(extensionQuery, BooleanClause.Occur.MUST);
            }

            // Add date range filter
            if (startDate != null && endDate != null) {
                Query dateQuery = LongPoint.newRangeQuery("modified",
                        startDate.getTime(), endDate.getTime());
                booleanQuery.add(dateQuery, BooleanClause.Occur.MUST);
            }

            TopDocs topDocs = searcher.search(booleanQuery.build(), 100);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                SearchResult result = new SearchResult();
                result.filePath = doc.get("path");
                result.fileName = doc.get("filename");
                result.owner = doc.get("owner");
                result.score = scoreDoc.score;
                results.add(result);
            }

        } catch (IOException e) {
            System.err.println("Metadata search failed: " + e.getMessage());
        }

        return results;
    }

    // ... rest of existing code ...
}