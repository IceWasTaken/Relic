package net.ice.relic.common.test.scene;

import net.ice.heirloom.color.Colors;
import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.test.gui.DebugGui;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector3f;

public class TestingScene extends Scene {

    public TestingScene(String name, RelicApplication application) {
        super(name, application);
    }

    @Override
    protected void sceneInit() {

        Model sponzaModel = getModelLoader().loadModel(Resource.getResource("relic", "resources/assets/models/sponza/Sponza.gltf"), false);
        Model footModel = getModelLoader().loadModel(Resource.getResource("relic", "resources/assets/models/foot/foot2.glb"), false);
        Model scarabModel = getModelLoader().loadModel(Resource.getResource("relic", "resources/assets/models/scarab/scarab.dae"), false);


//
//        Entity entity1 = createEntity("foot");
//        entity1.addComponent(new TransformComponent().setScale(10, 10, 10));
//        entity1.addComponent(new StaticModelComponent(footModel));
//        footModel.getSceneObjects().add(entity1);


//        Entity scarabEntity = createEntity("scarab");
//        scarabEntity.addComponent(new TransformComponent().setPosition(0, 0, 0));
//        scarabEntity.addComponent(new StaticModelComponent(scarabModel));
//        scarabModel.getSceneObjects().add(scarabEntity);

        Entity sponzaEntity = createEntity("sponza");
        sponzaEntity.addComponent(new TransformComponent().setPosition(0, 0, 0));
        sponzaEntity.addComponent(new StaticModelComponent(sponzaModel));
        sponzaModel.getSceneObjects().add(sponzaEntity);

        getLights().add(new Light(new Vector3f(1, -1, 2), false, 2, Colors.GRAY.getRGBColor()));
        setGUI(new DebugGui(application));

        camera.setPosition(-5, 3, 0);
        camera.setRotation((float) Math.toRadians(20.0f), (float) Math.toRadians(90));
    }

    @Override
    protected void sceneUpdate(float deltaTime) {
    }

    @Override
    protected void sceneDestroy() {

    }

}
