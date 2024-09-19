package mz.org.csaude.mentoring.model.resourceea;

import java.util.List;

import mz.org.csaude.mentoring.base.model.BaseModel;

public class Node extends BaseModel {

    public Node() {
    }
    private  String name;
    private String label;

    private Integer clickable;

    List<Node> children;

    private String icon;

    private String program;

    private String category;

    private String subCategory;

    private String resource;
    private String type;

    private String description;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getClickable() {
        return clickable;
    }

    public void setClickable(int clickable) {
        this.clickable = clickable;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
