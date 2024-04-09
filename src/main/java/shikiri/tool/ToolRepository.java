package shikiri.tool;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

@Repository
public interface ToolRepository extends CrudRepository<ToolModel, String> {
    // Find all tools
    @SuppressWarnings("null")
    List<ToolModel> findAll();

    // Find all tools by a partial match of their name
    List<ToolModel> findByNameContaining(String name, Sort sort);

    // Find tools by category with sorting capability (e.g., by most recently added)
    List<ToolModel> findByCategory(String category, Sort sort);

    // Example method to find all tools and order them, for instance by 'createdDate'
    List<ToolModel> findAllByOrderByCreatedDateDesc();
}