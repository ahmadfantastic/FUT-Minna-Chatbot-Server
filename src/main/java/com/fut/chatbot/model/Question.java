/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.search.DummyStringBridge;
import com.fut.chatbot.util.Constants;
import com.google.gson.annotations.Expose;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "question")
@XmlRootElement
@Indexed
@AnalyzerDefs({
    @AnalyzerDef(
            name = "text_analyzer",
            tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
            filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class)
                ,@TokenFilterDef(factory = StopFilterFactory.class,
                        params = {
                            @Parameter(name = "ignoreCase", value = "true")
                            ,
                            @Parameter(name = "words", value = "lucene/stopwords.txt")
                        }
                )
                ,@TokenFilterDef(factory = SnowballPorterFilterFactory.class,
                        params = {
                            @Parameter(name = "language", value = "English")
                        }
                )
                ,@TokenFilterDef(factory = SynonymFilterFactory.class,
                        name = "index",
                        params = {
                            @Parameter(name = "synonyms", value = "lucene/synonyms.txt")
                            ,
                            @Parameter(name = "ignoreCase", value = "true")
                            ,
                            @Parameter(name = "expand", value = "false")
                        }
                )
            }
    )
    ,
    @AnalyzerDef(
            name = "text_analyzer_query",
            tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
            filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class)
                ,@TokenFilterDef(factory = StopFilterFactory.class,
                        params = {
                            @Parameter(name = "ignoreCase", value = "true")
                        }
                )
                ,@TokenFilterDef(factory = SnowballPorterFilterFactory.class,
                        params = {
                            @Parameter(name = "language", value = "English")
                        }
                )
            }
    )
})
public class Question {

    public enum QuestionStatus {
        CREATED, REQUESTED, APPROVED
    };

    public enum QuestionExpiry {
        HOUR, DAY, WEEK, MONTH, SEMESTER, SESSION, NEVER
    };

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Expose
    @Fields({
        @Field(
                name = "body",
                index = Index.YES,
                analyze = Analyze.YES,
                analyzer = @Analyzer(definition = "text_analyzer")
        )
        ,
        @Field(
                name = "body_query",
                index = Index.YES,
                analyze = Analyze.YES,
                analyzer = @Analyzer(definition = "text_analyzer_query"),
                bridge = @FieldBridge(impl = DummyStringBridge.class)
        )
    })
    private String body;

    @Expose
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<QuestionTag> tags;

    @JoinColumn(name = "answer", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Answer answer;

    @JoinColumn(name = "contributor", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Contributor contributor;

    @Expose
    private QuestionStatus status;

    @Expose
    private QuestionExpiry expiry;

    @Expose
    private Date setupTime;

    public Question() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<QuestionTag> getTags() {
        return tags;
    }

    public void setTags(List<QuestionTag> tags) {
        this.tags = tags;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public QuestionExpiry getExpiry() {
        return expiry;
    }

    public void setExpiry(QuestionExpiry expiry) {
        this.expiry = expiry;
    }

    public Date getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Date setupTime) {
        this.setupTime = setupTime;
    }

    @Ignore
    public String toJSON() {
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
