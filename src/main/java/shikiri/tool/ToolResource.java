package shikiri.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.jsonwebtoken.security.Keys;
import shikiri.tool.exceptions.InvalidTokenException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/tools")
public class ToolResource {

    private final SecretKey secretKey;
    private final ToolService toolService;

    @Autowired
    public ToolResource(@Value("${shikiri.jwt.secret}") String secret, ToolService toolService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.toolService = toolService;
    }

    @PostMapping
    public ResponseEntity<Tool> createTool(@RequestBody Tool toolIn, @RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                ToolModel savedToolModel = toolService.saveTool(new ToolModel(toolIn));
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(savedToolModel.id()).toUri();
                return ResponseEntity.created(location).body(savedToolModel.toDTO());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Tool>> getAllTools(@RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Tool> tools = toolService.findAllTools().stream()
                                            .map(ToolModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(tools);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tool> getToolById(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                return toolService.findToolById(id)
                                .map(tool -> ResponseEntity.ok().body(tool.toDTO()))
                                .orElseGet(() -> ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/search/by-name")
    public ResponseEntity<List<Tool>> findToolsByNameContaining(@RequestParam String name,
                                                                @RequestParam(defaultValue = "name") String sortBy,
                                                                @RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Tool> tools = toolService.findToolsByNameContaining(name, Sort.by(sortBy)).stream()
                                            .map(ToolModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(tools);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/search/by-category")
    public ResponseEntity<List<Tool>> findToolsByCategory(@RequestParam String category,
                                                        @RequestParam(defaultValue = "name") String sortBy,
                                                        @RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Tool> tools = toolService.findToolsByCategory(category, Sort.by(sortBy)).stream()
                                            .map(ToolModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(tools);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/all/sorted-by-date")
    public ResponseEntity<List<Tool>> findAllToolsOrderedByCreationDateDesc(@RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                List<Tool> tools = toolService.findAllToolsOrderedByCreationDateDesc().stream()
                                            .map(ToolModel::toDTO)
                                            .collect(Collectors.toList());
                return ResponseEntity.ok().body(tools);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Tool> updateTool(@PathVariable String id, @RequestBody Tool toolIn, @RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
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
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTool(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        try {
            String userId = ToolUtility.getUserBySecretKey(headers, secretKey);
            if (userId != null) {
                boolean deleted = toolService.deleteTool(id);
                if (deleted) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
