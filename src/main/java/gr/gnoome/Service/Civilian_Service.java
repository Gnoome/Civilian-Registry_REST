package gr.gnoome.Service;
import gr.gnoome.Domain.Person;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/Civilians")
public class Civilian_Service{
    

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addperson(@Context UriInfo uriInfo, Person person){
        
        if (person == null){
            throw new BadRequestException("Civilian is empty");
        }

        if (person.getId().length()!= 8){
            throw new BadRequestException("Id is in wrong format");
        }
         if (person.getName().isEmpty()){
            throw new BadRequestException("Name is in wrong format");
        }
        if (person.getSurname().isEmpty()){
            throw new BadRequestException("Surname is in wrong format");
        }
        if (!person.getBirthdate().matches("\\d{2}-\\d{2}-\\d{4}")){
            throw new BadRequestException("Birthdate is in wrong format");
        }
        if(!person.getTax().matches("\\d{9}")&& person.getTax()!=null){
            throw new BadRequestException("Tax is in wrong format");
        }

        boolean IsItHere = DBHandler.checkcivilian(person.getId())
        if(IsItHere){
            throw new BadRequestException("Civilian alwready in the database");
        }else{
            DBHandler.addcivilian(person);
        }

        return Response.created(new URI(uriInfo.getPath()+"/"+person.getId())).build();

        }
    }
}