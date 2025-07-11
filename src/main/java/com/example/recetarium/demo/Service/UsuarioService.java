package com.example.recetarium.demo.Service;

import com.example.recetarium.demo.DTOs.*;
import com.example.recetarium.demo.Model.CodigoDeVerificacion;
import com.example.recetarium.demo.Model.Usuario;
import com.example.recetarium.demo.Repository.UsuarioRepository;
import com.example.recetarium.demo.Utiles.Notificador;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;
    @Lazy
    @Autowired
    Notificador notificador;
    @Lazy
    @Autowired
    private CodigoVerificacioService codigoVerificacioService;
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto requestDto) {//Creacion de usurio solo por MAIL Y ALIAS
        Usuario usuario = new Usuario();
        UsuarioResponseDto response = new UsuarioResponseDto();
        Optional<Usuario> op = repository.findByMailOrAlias(requestDto.getMail(), requestDto.getAlias());//chequea si ya existe

        if (op.isEmpty()) {//si no existe ni mail ni alias lo guardo
            usuario.setAlias(requestDto.getAlias());
            usuario.setMail(requestDto.getMail());
            usuario.setEstadoVerificacion(2);//SETEA QUE EL CODIGO ESTA PENDIENTE DE VERIFICAR

            //CREACION DEL CODIGO DE VERIFICACION DE LA CUENTA Y LO MACHEA CON USUARIO Y VICEVERSA
            CodigoDeVerificacion codigo=codigoVerificacioService.crearCodigoDeVerificacionCuenta(usuario);
            notificador.enviarMailCodigoVerificacion(requestDto.getMail(),requestDto.getAlias());//MANDA EL MAIL

            repository.save(usuario);
            response.setCodigo(910);
        } else {//VERIFICO si es el usuario tratando de verificar el codigo o si alguien quiere repetir valores
            if (op.get().getAlias().equals(requestDto.getAlias()) && op.get().getMail().equals(requestDto.getMail())) {//si el mail y el alias coincide chequea que que pueda ir a verificar
                if (op.get().getEstadoVerificacion() == 2 && !codigoVerificacioService.vencido(op.get().getIdUsuario())) {
                    response.setCodigo(910); //sigfinica que puede ir a verificar codigo
                } else if (op.get().getEstadoVerificacion() == 0) {
                    response.setCodigo(900);// significa que el codigo vencio y hay que noticiarle
                }
            } else {
                if (op.get().getAlias().equals(requestDto.getAlias())) {//COMPARO SI EL ALIAS ES IGUAL AL QUE INGRESO, EL ALIAS ES EL REPETIDO
                    response.setAlias(op.get().getAlias());
                    response.setCodigo(800);//REPETICION DE UN CAMPO
                } else {//LO INVERSO AL ANTERIOR, EL REPETIDO ES EL MAIL
                    response.setMail(op.get().getMail());
                    response.setCodigo(800);//REPETICION DE UN CAMPO
                }
            }
        }
            return response;
        }
        public void datosUsuario (UsuarioRequestDto requestDto)
        {//Actualizo datos de usuario (nombre, apellido, direccion) y lo guardo.Se actualiza por ID
            Optional<Usuario> user = repository.findByMailOrAlias(requestDto.getMail(), requestDto.getAlias());
            if(user.isPresent()) {
                Usuario usuario = user.get();
                usuario.setApellido(requestDto.getApellido());
                usuario.setNombre(requestDto.getNombre());
                usuario.setDireccion(requestDto.getDireccion());
                repository.save(usuario);
            }
        }
        public void cambiarEstadoVerificacion ( int codigo, Long idUsuario){
            Optional<Usuario> usuario = repository.findById(idUsuario);
            usuario.get().setEstadoVerificacion(codigo);
            repository.save(usuario.get());
        }
        public Long getId (String mail, String alias){
            Optional<Usuario> user = repository.findByMailOrAlias(mail, alias);
            if(user.isEmpty()){
                return Long.valueOf(-1);//NO EXISTE EL USUARIO CON ESOS CAMPOS
            }else {
                return user.get().getIdUsuario();
            }
        }
        public void guardarContraseniaa(String mail,String alias, String contrsenia){
            Optional<Usuario> user = repository.findByMailOrAlias(mail, alias);
            if(user.isPresent()){
                Usuario usuario=user.get();
                usuario.setContrasenia(contrsenia);
                repository.save(usuario);

            }
        }
        @Transactional
        public LogginResponseDto credencialesCorrectas(LogginRequestDto request){//request es el mail o alias
            LogginResponseDto responseDto=new LogginResponseDto();
            Optional<Usuario> user=repository.findByMailOrAlias(request.aliasOMail,request.aliasOMail);
            if(user.isEmpty()){
                responseDto.setCodigo(400);//No existe nadie con ese mail o alias
            }else{
                if(user.get().getContrasenia().equals(request.contrasenia)){
                    responseDto.setCodigo(200);//Todo Ok la contrasnia es esa
                    responseDto.setId(user.get().getIdUsuario());
                    responseDto.setAlias(user.get().getAlias());
                    if(user.get().getAlumno()==null){
                        responseDto.setAlumno(null);
                    }else {
                        responseDto.setAlumno(user.get().getAlumno().getIdAlumno());
                    }
                }
                else{
                    responseDto.setCodigo(777);//contra mala
                }
            }
            return responseDto;
        }

        public Optional<Usuario> getUsuarioPorMailOAlias(String alias,String mail){
            return repository.findByMailOrAlias(alias,mail);
        }
    }