package shikiri.tool;

public class ToolParser {

    public static Tool to(ToolIn in) {
        return Tool.builder()
            .name(in.name())
            .category(in.category())
            .description(in.description())
            .userId(in.userId())
            .build();
    }

    public static ToolOut to(Tool tool) {
        return ToolOut.builder()
            .id(tool.id())
            .name(tool.name())
            .userId(tool.userId())
            .build();
    }
    
}
