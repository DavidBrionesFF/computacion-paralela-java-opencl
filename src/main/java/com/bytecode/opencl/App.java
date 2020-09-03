package com.bytecode.opencl;

import com.jogamp.opencl.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import static java.lang.System.out;

import java.nio.FloatBuffer;
import java.util.Scanner;


public class App {
    public static void main(String[] args) {
        int VECTOR_SIZE = 1024;

        CLContext clContext = CLContext.create(CLDevice.Type.GPU);

        out.println(clContext.getDevices()[0].getName());
        CLDevice device = clContext.getDevices()[0];

        Scanner scanner;
        String source_str = "";
        try{
            scanner = new Scanner(new FileReader(App.class.getClassLoader().getResource("vector_sum.cl").getFile()));
            while ( scanner.hasNextLine() ){
                source_str += scanner.nextLine();
            }
            scanner.close();
        }
        catch (FileNotFoundException ex){
            out.println(ex);
        }

        CLProgram program = clContext.createProgram(source_str).build();

        CLBuffer<FloatBuffer> bufferv1 = clContext.createFloatBuffer(VECTOR_SIZE, CLMemory.Mem.READ_ONLY);
        CLBuffer<FloatBuffer> bufferv2 = clContext.createFloatBuffer(VECTOR_SIZE, CLMemory.Mem.READ_ONLY);
        CLBuffer<FloatBuffer> bufferv3 = clContext.createFloatBuffer(VECTOR_SIZE, CLMemory.Mem.WRITE_ONLY);

        for(int i = 0; i < VECTOR_SIZE; i++){
            bufferv1.getBuffer().put((float)i / 10);
            bufferv2.getBuffer().put(-(float)i / 9);
        }
        bufferv1.getBuffer().rewind();
        bufferv2.getBuffer().rewind();

        CLKernel kernel = program.createCLKernel("vector_sum");

        kernel.putArgs(bufferv1, bufferv2, bufferv3);

        CLCommandQueue command = device.createCommandQueue();

        command.putWriteBuffer(bufferv1, false);
        command.putWriteBuffer(bufferv2, false);
        command.put1DRangeKernel(kernel, 0, VECTOR_SIZE, 1);

        for(int i = 0; i < 100; i++){
            out.println( bufferv1.getBuffer().get(i)
                    + " + " + bufferv2.getBuffer().get(i)
                    + " = " + bufferv3.getBuffer().get() );
        }

    }
}
