package zc.ai.service.rag;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zc.ai.service.example.Assistant;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
@Service
public class RagService {
    private static final Logger logger =  LoggerFactory.getLogger(RagService.class);

    @Autowired
    private  OpenAiChatModel chatModel;
    @Autowired
    private  EmbeddingModel embeddingModel;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Value("${zc.ai.service.project-dir}")
    String documentsPath = null;
    public RagResponse processQuestion(RagRequest request) throws IOException {

        String question = request.getQuestion();
        Assistant assistant = this.createAssistantWithFileContent(request.getDocName());

        String answer = assistant.chat(request.getQuestion());
        // 5. 返回响应
        return new RagResponse(answer);
    }

    public  Assistant createAssistantWithFileContent(String filePath) {

        filePath=this.documentsPath+filePath;
        // First, let's load documents that we want to use for RAG
        Document document = loadDocument(filePath);
        if("miles-of-smiles-terms-of-use.doc".equals(filePath)){
            System.out.println("get the doc");
        }

        MessageWindowChatMemory mem=null;
        if(filePath!=null){
            mem = MessageWindowChatMemory.withMaxMessages(1);
            String txtMsg = getTxt2String(filePath);
            mem.add(AiMessage.from(txtMsg));
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel) // it should use OpenAI LLM
                .chatMemory(mem) // it should remember 10 latest messages
                //.contentRetriever(createContentRetriever(documents)) // it should have access to our documents
                .build();

        return assistant;
    }
    public  Assistant createAssistant() {

        MessageWindowChatMemory mem=null;
            mem = MessageWindowChatMemory.withMaxMessages(20);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel) // it should use OpenAI LLM
                .chatMemory(mem) // it should remember 10 latest messages
                .build();

        return assistant;
    }

    //获取文本文件的内容
    private String getTxt2String(String fileName) {

        String txt =null;
        try{
            logger.info("创建聊天上下文");
            txt = Files.readString(Path.of(fileName));
        } catch (IOException e) {
            logger.error("出错了");
            txt="";
        }
        return txt;
    }

    private static ContentRetriever createContentRetriever(List<Document> documents) {

        // Here, we create an empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

}