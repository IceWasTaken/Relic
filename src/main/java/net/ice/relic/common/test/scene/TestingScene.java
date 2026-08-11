package net.ice.relic.common.test.scene;

import net.ice.curio.input.Input;
import net.ice.heirloom.color.Colors;
import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.test.gui.DebugGui;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Random;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;

public class TestingScene extends Scene {

    public TestingScene(String name, RelicApplication application) {
        super(name, application);
    }

    private int lastCubeCount = 0;

    @Override
    protected void sceneInit() {

        Model scarabModel = getModelLoader().loadModel(Resource.getResource("relic", "assets/models/scarab/scarab.dae"), false);
        Model sponzaModel = getModelLoader().loadModel(Resource.getResource("relic", "assets/models/sponza/Sponza.gltf"), false);
        Model companionCube = getModelLoader().loadModel(Resource.getResourceFromString("relic:assets/models/CompanionCubes/EDITOR_companion_cube.obj"), false);

        Entity scarabEntity = createEntity("scarab");
        scarabEntity.addComponent(new TransformComponent().setPosition(0, 0, 0));
        scarabEntity.addComponent(new StaticModelComponent(scarabModel));
        scarabModel.getEntities().add(scarabEntity);

        Entity sponzaEntity = createEntity("sponza");
        sponzaEntity.addComponent(new TransformComponent().setPosition(0, 0, 0));
        sponzaEntity.addComponent(new StaticModelComponent(sponzaModel));
        sponzaModel.getEntities().add(sponzaEntity);

        Entity sponzaEntity1 = createEntity("sponza1");
        sponzaEntity1.addComponent(new TransformComponent().setPosition(0, 0, 20));
        sponzaEntity1.addComponent(new StaticModelComponent(sponzaModel));
        sponzaModel.getEntities().add(sponzaEntity1);

//        Entity cube = createEntity("cube");
//        cube.addComponent(new TransformComponent());
//        cube.addComponent(new StaticModelComponent(companionCube));
//        companionCube.getEntities().add(cube);

        getLights().add(new Light(new Vector3f(1, -1, 2), false, 2, Colors.GRAY.getRGBColor()));
        setGUI(new DebugGui(application));

        camera.setPosition(-5, 3, 0);
        camera.setRotation((float) Math.toRadians(20.0f), (float) Math.toRadians(90));
    }

    @Override
    protected void sceneUpdate(float deltaTime) {
        if(Input.isKeyDown(GLFW_KEY_N)) {
            Random random = new Random();

            Model companionCube = getApplication().getModelCache().getModel("EDITOR_companion_cube.obj");

            Entity cube = createEntity("cube" + lastCubeCount);
            cube.addComponent(
                    new TransformComponent()
                            .setPosition(
                                    random.nextInt(100),
                                    random.nextInt(100),
                                    random.nextInt(100)
                            )
            );
            cube.addComponent(new StaticModelComponent(companionCube));
            companionCube.getEntities().add(cube);

            ((GLRenderer) getApplication().getRenderer()).getBufferManager().loadEntity(cube);

            lastCubeCount++;
        }
    }

    @Override
    protected void sceneDestroy() {

    }

}
