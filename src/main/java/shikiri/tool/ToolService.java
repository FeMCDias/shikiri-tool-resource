package shikiri.tool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

@Service
public class ToolService {

    private ToolRepository toolRepository;
    private SecretKey secretKey;

    @Autowired
    public ToolService(ToolRepository toolRepository, @Value("${shikiri.jwt.secret}") String secret) {
        this.toolRepository = toolRepository;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Tool create(Tool toolIn, String authToken) {
        toolIn.userId(ToolUtility.getUserIdFromToken(authToken, secretKey));
        return toolRepository.save(new ToolModel(toolIn)).to();
    }

    public List<Tool> findAll(String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findAllByUserId(userId)
                .orElseGet(Collections::emptyList) // Return an empty list if Optional is empty
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    public Tool findById(String id, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByIdAndUserId(id, userId)
                .map(ToolModel::to)
                .orElse(null); // Return null if Optional is empty
    }

    public List<Tool> findByNameContaining(String name, String sortBy, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByNameContainingAndUserId(name, Sort.by(sortBy), userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    public List<Tool> findByCategory(String category, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByCategoryAndUserId(category, userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    public List<Tool> findAllOrderedByCreationDateDesc(String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findAllByUserIdOrderByCreatedDateDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

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

    public boolean delete(String id, String authToken) {
        String userId = ToolUtility.getUserIdFromToken(authToken, secretKey);
        return toolRepository.findByIdAndUserId(id, userId)
                .map(tool -> {
                    toolRepository.deleteById(tool.id());
                    return true;
                })
                .orElse(false);
    }
}
