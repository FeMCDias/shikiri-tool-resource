package shikiri.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/tools")
public class ToolResource {

    @Autowired
    private ToolService toolService;

    @PostMapping
    public ResponseEntity<Tool> createTool(@RequestBody Tool toolIn, @RequestHeader String idUser) {
        toolIn.userId(idUser);
        ToolModel savedToolModel = toolService.saveTool(new ToolModel(toolIn));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedToolModel.id()).toUri();
        return ResponseEntity.created(location).body(savedToolModel.toDTO());
    }

    @GetMapping
    public ResponseEntity<List<Tool>> getAllTools(@RequestHeader String idUser) {
        List<Tool> tools = StreamSupport.stream(toolService.findAllTools().spliterator(), false)
                .map(ToolModel::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tools);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tool> getToolById(@PathVariable String id, @RequestHeader String idUser) {
        Optional<ToolModel> toolModel = toolService.findToolById(id);
        return toolModel.map(model -> ResponseEntity.ok().body(model.toDTO()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<Tool>> findToolsByNameContaining(@RequestParam String name,
                                                                @RequestParam(defaultValue = "name") String sortBy) {
        List<Tool> tools = toolService.findToolsByNameContaining(name, Sort.by(sortBy)).stream()
                .map(ToolModel::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tools);
    }

    @GetMapping("/search/by-category")
    public ResponseEntity<List<Tool>> findToolsByCategory(@RequestParam String category,
                                                          @RequestParam(defaultValue = "name") String sortBy) {
        List<Tool> tools = toolService.findToolsByCategory(category, Sort.by(sortBy)).stream()
                .map(ToolModel::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tools);
    }

    @GetMapping("/all/sorted-by-date")
    public ResponseEntity<List<Tool>> findAllToolsOrderedByCreationDateDesc() {
        List<Tool> tools = toolService.findAllToolsOrderedByCreationDateDesc().stream()
                .map(ToolModel::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tools);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tool> updateTool(@PathVariable String id, @RequestBody Tool toolIn) {
        Optional<ToolModel> existingToolModelOptional = toolService.findToolById(id);
        if (!existingToolModelOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ToolModel existingToolModel = existingToolModelOptional.get();
        existingToolModel.name(toolIn.name());
        existingToolModel.category(toolIn.category());
        existingToolModel.description(toolIn.description());
        ToolModel updatedToolModel = toolService.updateTool(existingToolModel);
        return ResponseEntity.ok().body(updatedToolModel.toDTO());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTool(@PathVariable String id) {
        Optional<ToolModel> toolModel = toolService.findToolById(id);
        if (toolModel.isPresent()) {
            toolService.deleteTool(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
