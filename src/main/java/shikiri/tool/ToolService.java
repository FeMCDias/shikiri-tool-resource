package shikiri.tool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class ToolService {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    @CachePut(value = "toolCache", key = "#result.id")
    public Tool create(Tool toolIn) {
        return toolRepository.save(new ToolModel(toolIn)).to();
    }
    
    @CachePut(value = "toolCache", key = "#id")
    public Tool update(String id, Tool toolIn) {
        return toolRepository.findById(id)
                .map(existingToolModel -> {
                    existingToolModel.name(toolIn.name())
                                      .category(toolIn.category())
                                      .description(toolIn.description());
                    return toolRepository.save(existingToolModel).to();
                })
                .orElse(null);
    }    

    @CacheEvict(value = "toolCache", key = "#id")
    public boolean delete(String id) {
        if(toolRepository.findById(id).isPresent()) {
            toolRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Cacheable(value = "toolCache", key = "'findAll-userId:' + #userId")
    public List<Tool> findAll(String userId) {
        return toolRepository.findByUserIdOrderByNameDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "toolCache", key = "'findById:' + #id")
    public Tool findById(String id) {
        return toolRepository.findById(id)
                .map(ToolModel::to)
                .orElse(null);
    }

    @Cacheable(value = "toolCache", key = "'findByName-userId:' + #userId + '-name:' + #name + '-sortBy:' + #sortBy")
    public List<Tool> findByNameContaining(String name, String userId, String sortBy) {
        return toolRepository.findByNameContainingAndUserId(name, userId, Sort.by(sortBy))
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "toolCache", key = "'findByCategory-userId:' + #userId + '-category:' + #category + '-sortBy:' + #sortBy")
    public List<Tool> findByCategory(String category, String userId, String sortBy) {
        return toolRepository.findByCategoryAndUserId(category, userId, Sort.by(sortBy))
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "toolCache", key = "'findOrderByName-userId:' + #userId")
    public List<Tool> findOrderByName(String userId) {
        return toolRepository.findByUserIdOrderByNameDesc(userId)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ToolModel::to)
                .collect(Collectors.toList());
    }
}