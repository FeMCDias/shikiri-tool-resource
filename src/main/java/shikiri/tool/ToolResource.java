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
    public ResponseEntity<ToolOut> create(String authToken, ToolIn toolIn) {
        try {
            Tool tool = ToolParser.to(toolIn);
            tool = toolService.create(tool, authToken);
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
    public ResponseEntity<ToolOut> update(String authToken, ToolIn toolIn) {
        try {
            Tool tool = ToolParser.to(toolIn);
            tool = toolService.update(tool.id(), tool, authToken);
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
    public ResponseEntity<ToolOut> delete(String authToken, String id) {
        try {
            boolean deleted = toolService.delete(id, authToken);
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
    public ResponseEntity<ToolOut> getById(String authToken, String id) {
        try {
            Tool tool = toolService.findById(id, authToken);
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
    public ResponseEntity<List<ToolOut>> findByNameContaining(String authToken, String name, String sortBy) {
        try {
            List<Tool> tools = toolService.findByNameContaining(name, sortBy, authToken);
            List<ToolOut> toolsOut = tools.stream().map(ToolParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(toolsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ToolOut>> findByCategory(String authToken, String category, String sortBy) {
        try {
            List<Tool> tools = toolService.findByCategory(category, authToken);
            List<ToolOut> toolsOut = tools.stream().map(ToolParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(toolsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ToolOut>> findAllOrderedByCreationDateDesc(String authToken) {
        try {
            List<Tool> tools = toolService.findAllOrderedByCreationDateDesc(authToken);
            List<ToolOut> toolsOut = tools.stream().map(ToolParser::to).collect(Collectors.toList());
            return ResponseEntity.ok(toolsOut);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
