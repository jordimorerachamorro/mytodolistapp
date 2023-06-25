package com.jordimorera.mytodolistapp.mapper;

public interface IMapper <I, O>{
    public O map(I in);
}
