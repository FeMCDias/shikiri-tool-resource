package shikiri.tool;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.domain.Sort;

@Repository
public interface ToolRepository extends CrudRepository<ToolModel, String> {
    // Find all tools
    @SuppressWarnings("null")

    // Find all tools by user ID ordered by name - Can be empty
    Optional<List<ToolModel>> findByUserIdOrderByNameDesc(String userId);
    
    // Find a tool by ID and user ID - Can be Empty
    Optional<ToolModel> findById(String id);

    // Find all tools by user ID and name containing - Can be empty
    Optional<List<ToolModel>> findByNameContainingAndUserId(String name, String userId, Sort sort);

    // Find all tools by user ID and category - Can be empty
    Optional<List<ToolModel>> findByCategoryAndUserId(String category, String userId, Sort sort);
}