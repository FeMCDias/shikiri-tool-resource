package shikiri.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shikiri.tool.exceptions.InvalidTokenException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ToolResource implements ToolController {

    @Autowired
    private ToolService toolService;

    @Override
    public ResponseEntity<ToolOut> create(String userId, ToolIn toolIn) {
        try {
            Tool tool = ToolParser.to(toolIn);
            tool.userId(userId);
            tool = toolService.create(tool);
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tool.id())
                .toUri();
            return ResponseEntity.created(location).body(ToolParser.to(tool));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ToolOut> update(ToolIn toolIn) {
        try {
            Tool tool = ToolParser.to(toolIn);
            tool = toolService.update(tool.id(), tool);
            if (tool != null) {
                return ResponseEntity.ok(ToolParser.to(tool));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ToolOut> delete(String id) {
        try {
            boolean deleted = toolService.delete(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ToolOut> getById(String id) {
        try {
            Tool tool = toolService.findById(id);
            if (tool != null) {
                return ResponseEntity.ok(ToolParser.to(tool));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ToolOut>> findByNameContaining(String name, String userId, String sortBy) {
        try {
            List<Tool> tools = toolService.findByNameContaining(name, userId, sortBy);
            List<ToolOut> toolsOut = tools.stream().map(ToolParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(toolsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ToolOut>> findByCategory(String category, String userId, String sortBy) {
        try {
            List<Tool> tools = toolService.findByCategory(category, userId, sortBy);
            List<ToolOut> toolsOut = tools.stream().map(ToolParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(toolsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ToolOut>> findOrderByName(String userId) {
        try {
            List<Tool> tools = toolService.findOrderByName(userId);
            List<ToolOut> toolsOut = tools.stream().map(ToolParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(toolsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
