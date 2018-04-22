package com.eci.cosw.springbootsecureapi.controller;


import com.eci.cosw.springbootsecureapi.model.Person;
import com.eci.cosw.springbootsecureapi.model.User;
import com.eci.cosw.springbootsecureapi.service.PersonService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping( "person" )
public class PersonController {

    @Autowired
    private PersonService personService;

    @CrossOrigin
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> newPerson(@RequestBody Person person) {
        return new ResponseEntity<>(personService.createPerson(person), HttpStatus.ACCEPTED);
    }


    @CrossOrigin
    @RequestMapping( value = "/login", method = RequestMethod.POST )
    public ResponseEntity<?> login(@RequestBody User login ) {


        String jwtToken = "";

        if ( login.getUsername() == null || login.getPassword() == null )
        {
            return new ResponseEntity<>( "Please fill in username and password", HttpStatus.FORBIDDEN );
        }

        String username = login.getUsername();
        String password = login.getPassword();

        Person user = personService.getUser( username );

        if ( user == null )
        {
            return new ResponseEntity<>( "User username not found.", HttpStatus.FORBIDDEN );
        }

        String pwd = user.getPassword();

        if ( !password.equals( pwd ) )
        {
            return new ResponseEntity<>( "Invalid login. Please check your name and password.", HttpStatus.FORBIDDEN );
        }

        jwtToken = Jwts.builder().setSubject( username ).claim( "roles", "user" ).setIssuedAt( new Date() ).signWith(
                SignatureAlgorithm.HS256, "secretkey" ).compact();
        return new ResponseEntity<>( new PersonController.Token(jwtToken) ,HttpStatus.ACCEPTED);
    }

    public class Token
    {

        String access_token;


        public Token( String access_token )
        {
            this.access_token = access_token;
        }


        public String getAccessToken()
        {
            return access_token;
        }

        public void setAccessToken( String access_token )
        {
            this.access_token = access_token;
        }
    }

}
