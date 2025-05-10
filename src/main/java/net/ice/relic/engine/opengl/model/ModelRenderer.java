package net.ice.relic.engine.opengl.model;

public class ModelRenderer {

    private Model model;

    public ModelRenderer(String modelPath, int flags) {
        this.model = new Model(modelPath, flags);
    }

    public void render(int shaderProgram) {
        model.render(shaderProgram);
    }

    public void cleanup() {
        model.cleanup();
    }
}