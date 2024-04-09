package shikiri.tool;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import shikiri.tool.exceptions.CustomDataAccessException;
import shikiri.tool.exceptions.CustomDataIntegrityViolationException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    private final ToolRepository toolRepository;

    // Constructor-based injection is recommended for better testability
    @Autowired
    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    // Method to create (or save) a tool
    @Transactional
    public ToolModel saveTool(ToolModel tool) {
        try {
            return toolRepository.save(tool);
        } catch (DataIntegrityViolationException e) {
            throw new CustomDataIntegrityViolationException("Failed to save tool due to data integrity violation.");
        } catch (DataAccessException e) {
            throw new CustomDataAccessException("Failed to access data.");
        }
    }

    // Method to retrieve all tools
    public List<ToolModel> findAllTools() {
        return toolRepository.findAll();
    }

    // Method to find a tool by its ID
    public Optional<ToolModel> findToolById(String id) {
        return toolRepository.findById(id);
    }
    
    // Method to find tools by name containing a specific string, sorted by a criteria
    public List<ToolModel> findToolsByNameContaining(String name, Sort sort) {
        return toolRepository.findByNameContaining(name, sort);
    }

    // Method to find tools by category, also sorted
    public List<ToolModel> findToolsByCategory(String category, Sort sort) {
        return toolRepository.findByCategory(category, sort);
    }

    // Method to retrieve all tools ordered by creation date in descending order
    public List<ToolModel> findAllToolsOrderedByCreationDateDesc() {
        return toolRepository.findAllByOrderByCreatedDateDesc();
    }

    // Method to update a tool
    @Transactional
    public ToolModel updateTool(ToolModel tool) {
        return toolRepository.save(tool);
    }

    // Method to delete a tool by its ID
    @Transactional
    public Boolean deleteTool(String id) {
        try {
            toolRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new CustomDataAccessException("Failed to access data.");
        }
    }
}
