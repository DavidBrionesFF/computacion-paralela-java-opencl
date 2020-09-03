__kernel void vector_sub(__global float *v1, __global float *v2, __global float *v3)
{
    int i = get_global_id(0);
    v3[i] = v1[i] - v2[i];
}