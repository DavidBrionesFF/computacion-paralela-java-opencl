__kernel void vector_find_int(__global int *v1, __global int *value, __global int *pos)
{
    int i = get_global_id(0);

    if(v1[i] == value[0]){
        pos[0] = i;
    }
}