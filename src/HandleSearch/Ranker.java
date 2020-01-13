package HandleSearch;

import HandleSearch.DocDataHolders.DocRankData;
import TermsAndDocs.Terms.Term;

import java.util.ArrayList;

/**
 * This class is responsible for ranking documents with respect to a query
 */
public class Ranker {

    /**
     * Field mentioning if we should take into account the result of the semantic connection
     * between the query and the document. Probably will be decided by the user.
     * If it is false, only BM25 will be taken into account.
     */
    private boolean isSemantic;


    /**
     * The percentage given to the ranking functions on the original query words when calculating the rank in case of taking into account
     * the semantically close words.
     * It is slitted by two for the BM25 an the TFIDF ranking methods.
     * 1 minus this value is the percentage given to the semantic similarities words.
     */
    //0.6
    private final double weightOfOriginalQuery = 0.85;

    /**
     * The percentage given to the bm25 functions when scoring with the different parameters
     * The reat goes to cos similarity, is the word in the header, etc...
     */
    private final double weightOfBM25 = 0.6;


    /**
     * k1 parameter for bm25
     */
    //2
    //1.2 134
    private double k1 = 1.2;

    /**
     * b parameter for bm25
     */
    //0.8
    //0.865 134
    private double b = 0.865;

    /**
     * This is the number of documents in the corpus
     */
    public final int numOfDocs = 472522;

    /**
     * This is the avg doc length
     */
    public final int avgDocLength = 250;

    /**
     * @param isSemantic field mentioning if we should take into account the result of the semantic connection
     */
    public Ranker(boolean isSemantic) {
        this.isSemantic = isSemantic;
    }

    /**
     * computes the final ranking of the document, by calculating BM25 and TfIdf ranking of the
     * original query, and if {@code isSemantic} is true with the semantic close words also.
     * @return ranking
     */
    public double rankDocument(DocRankData docRankData) {
        double output;
        double bM25ofQuery = getBM25Rank(docRankData.getQueryWordsTfs(), docRankData.getQueryWordsDfs(), docRankData.getLengthOfDoc());
        double termsInHeaderScoreQuery = getTermsInHeaderScore(docRankData.getQueryWords(), docRankData.getDocHeaderStrings());
        double cosSimRankQuery = getCosSimRank(docRankData.getQueryWordsTfs(), docRankData.getQueryWordsDfs());
        double queryScore = weightOfBM25 * bM25ofQuery + 0.05 * termsInHeaderScoreQuery + (1 - 0.05 - weightOfBM25) * cosSimRankQuery;
        if (!isSemantic) {
            output = queryScore;
        } else { //with semantics
            double bM25OfSemantic = getBM25Rank(docRankData.getSimilarWordsTfs(), docRankData.getSimilarWordsDfs(), docRankData.getLengthOfDoc());
            double termsInHeaderScoreSimilar = 0.05 * getTermsInHeaderScore(docRankData.getSimilarWords(), docRankData.getDocHeaderStrings());
            double cossimSimilar = getCosSimRank(docRankData.getSimilarWordsTfs(), docRankData.getSimilarWordsDfs());

            output = weightOfOriginalQuery * queryScore
                    + (1 - weightOfOriginalQuery) * (weightOfBM25 * bM25OfSemantic + 0.05 * termsInHeaderScoreSimilar + (1 - 0.05 - weightOfBM25) * cossimSimilar);
        }
        return output;
    }

    private double getCosSimRank(ArrayList<Integer> tfs, ArrayList<Integer> dfs) {
        double[] queryVector = new double[tfs.size()];
        for (int i = 0; i < queryVector.length; i++) {
            queryVector[i] = 1;
        } 

        double[] docVector = new double[tfs.size()];
        for (int i = 0; i < docVector.length; i++) {
            docVector[i] = tfs.get(i) * getIdf(dfs.get(i));
        }
        return cosineSimilarity(queryVector, docVector);
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        double scoreCOS = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return scoreCOS;
    }

    private double getBM25Rank(ArrayList<Integer> tfs, ArrayList<Integer> dfs, int lengthOfDoc) {
        double output = 0;
        for (int i = 0; i < tfs.size(); i++) {
            output += getBM25ForOneTerm(tfs.get(i), dfs.get(i), lengthOfDoc);
        }
        return output;
    }

    private double getBM25ForOneTerm(Integer tf, Integer df, int lengthOfDoc) {
        double numerator = (tf) * (k1 + 1);
        double denominatorFraction = (double)lengthOfDoc / (double)avgDocLength;
        double denominator = (tf + k1 * (1 - b + b * (denominatorFraction)));
        double fraction = numerator/denominator;
        double result = getIdf(df) * fraction;
        return result;
    }

    /**
     * @param term
     * @param docHeaderStrings
     * @return 1 if header contains un-stemmed version of the term supllied, else false
     */
    private int isTermInHeader(Term term, ArrayList<Term> docHeaderStrings) {
        return docHeaderStrings.contains(term) ? 1 : 0;
    }

    /**
     * returns the percentage of the words from the query that are in the documents header
     *
     * @param terms
     * @param docHeaderStrings
     * @return the percentage of the words from the query that are in the documents header
     */
    private double getTermsInHeaderScore(ArrayList<Term> terms, ArrayList<Term> docHeaderStrings) {
        int counter = 0;
        for (Term term : terms) {
            counter += isTermInHeader(term, docHeaderStrings); //this will give us the number of terms from the list which are in the header
        }
        return ((double) counter) / ((double) terms.size());
    }

    /**
     * @param df
     * @return idf of given df, based on {@code numOfDocs} field
     */
    private double getIdf(int df) {
        //return ((Math.log(numOfDocs/df)) / Math.log(2));
        double idf=(Math.log10((numOfDocs-df+0.5) / ((df+0.5)))) / Math.log10(2);
        return idf;
    }


}
