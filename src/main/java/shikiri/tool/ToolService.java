package shikiri.tool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import io.jsonwebtoken.security.Keys;

@Service
public class ToolService {

    @Autowired
    private ToolRepository toolRepository;
    private SecretKey secretKey;

    @Autowired
    public ToolService(ToolRepository toolRepository, @Value("${shikiri.jwt.secretKey}") String secret) {
        this.toolRepository = toolRepository;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @CachePut(value = "toolCache", key = "#result.id")
    public Tool create(Tool toolIn, String authToken) {
        toolIn.userId(ToolUtility.getUserIdFromToken(authToken, secretKey));
        return toolRepository.save(new ToolModel(toolIn)).to();
    }

    @CachePut(value = "toolCache", key = "#toolIn.id")
    public Tool update(String id, Tool toolIn, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByIdAndUserId(id, userId)
                .map(existingToolModel -> {
                    existingToolModel.name(toolIn.name())
                                      .category(toolIn.category())
                                      .description(toolIn.description());
                    return toolRepository.save(existingToolModel).to();
                })
                .orElse(null);
    }

    @CacheEvict(value = "toolCache", key = "#id")
    public boolean delete(String id, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        if(toolRepository.findByIdAndUserId(id, userId).isPresent()) {
            toolRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Cacheable(value = "toolCache", key = "#authToken")
    public List<Tool> findAll(String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findAllByUserId(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "toolCache", key = "#id")
    public Tool findById(String id, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByIdAndUserId(id, userId)
                .map(ToolModel::to)
                .orElse(null);
    }

    @Cacheable(value = "toolCache", key = "{#authToken, #name}")
    public List<Tool> findByNameContaining(String name, String sortBy, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByNameContainingAndUserId(name, Sort.by(sortBy), userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "toolCache", key = "{#authToken, #category}")
    public List<Tool> findByCategory(String category, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByCategoryAndUserId(category, userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "toolCache", key = "#authToken")
    public List<Tool> findOrderByName(String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByUserIdOrderByNameDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }
}
