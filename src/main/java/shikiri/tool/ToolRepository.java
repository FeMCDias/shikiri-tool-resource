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

    // Find all tools by user ID - Can be empty
    Optional<List<ToolModel>> findAllByUserId(String userId);

    // Find a tool by ID and user ID - Can be Empty
    Optional<ToolModel> findByIdAndUserId(String id, String userId);

    // Find all tools by user ID and name containing - Can be empty
    Optional<List<ToolModel>> findByNameContainingAndUserId(String name, Sort sort, String userId);

    // Find all tools by user ID and category - Can be empty
    Optional<List<ToolModel>> findByCategoryAndUserId(String category, String userId);

    // Find all tools by user ID ordered by name - Can be empty
    Optional<List<ToolModel>> findByUserIdOrderByNameDesc(String userId);
}