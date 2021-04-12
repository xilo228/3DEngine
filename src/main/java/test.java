import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;


import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class test {
    boolean firstMouse = true;
    float lastX = w/2f, lastY = h/2f;
    float yaw = -90f,pitch = 0f;
    private boolean[] keys = new boolean[1024];
    private float lastFrame = 0, deltaTime = 0;
    private static long window;
    public static final int w = 1920, h = 1080;
    private static int VBO, VAO, IBO, texture;

    public void start() throws IOException {
        if (!glfwInit()) {
            throw new RuntimeException("sucks");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);


        window = glfwCreateWindow(w, h, "Engine", NULL, NULL);
        if (window == NULL) {
            cleanup();
            throw new RuntimeException("sucks");
        }
        glfwFocusWindow(window);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        Shader shader = new Shader("D:\\3DEngine\\src\\main\\java\\vertexShader.glsl",
                "D:\\3DEngine\\src\\main\\java\\fragmentShader.glsl");
        Camera camera = new Camera(window, shader, w, h);
        key_callback(camera);
        mouse_callback();

        CubeMesh cube = new CubeMesh("D:\\3DEngine\\src\\main\\resources\\container.jpg", shader);

        glViewport(0, 0, w, h);

        Vec3[][][] tmp = new Vec3[16][16][128];

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 128; y++) {
                    tmp[z][x][y] = new Vec3(x,y,z);
                }
            }
        }


//        for (Vec3[][] i: tmp){
//            for(Vec3[] z: i){
//                System.out.println(Arrays.toString(z));
//                cube.addCube(z);
//            }
//        }

        cube.addCube(new Vec3[]{new Vec3(1, 1, 1)});
        glfwSwapInterval(0);

        while (!glfwWindowShouldClose(window)) {
            float curFrame = (float) glfwGetTime();

            deltaTime = curFrame - lastFrame;
            lastFrame = curFrame;
            System.out.println(1 / deltaTime);
            glfwPollEvents();

            //начало отрисовки
            glEnable(GL_DEPTH_TEST);
            GL46.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            cube.draw();
            shader.Use();
            camera.Use();
            camera.do_movement(keys, deltaTime);
            camera.mouse_movement(pitch, yaw);
            Mat4 value = new Mat4();

            int location = glGetUniformLocation(shader.Program, "transform");
            glUniformMatrix4fv(location, false, value.getArray());

            glfwSwapBuffers(window);
        }
        cleanup();
    }

    void key_callback(Camera camera) {

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

            if (action == GLFW_PRESS) {
                keys[key] = true;
            }
            else if(action == GLFW_RELEASE){
                keys[key] = false;
            }
            if (keys[GLFW_KEY_ESCAPE]) {
                glfwSetWindowShouldClose(window, true);
            }
            }
        );
    }

    void mouse_callback(){
        glfwSetCursorPos(window, lastX, lastY);
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {

            float xoffset = (float) (xpos - lastX);
            float yoffset = (float) (lastY - ypos);
            lastX = (float)xpos;
            lastY = (float)ypos;
            float sensitivity = 0.006f;
            xoffset *= sensitivity;
            yoffset *= sensitivity;
            yaw   += xoffset;
            pitch += yoffset;
            if(pitch > 1.5f)
                pitch =  1.5f;
            if(pitch < -1.5f)
                pitch = -1.5f;

        });
    }


    public static void cleanup() {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glfwDestroyWindow(window);
        glfwTerminate();
    }


    public static void main(String[] args) throws IOException {
        new test().start();
    }
}