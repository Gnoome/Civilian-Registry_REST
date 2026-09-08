package gr.gnoome.Service;

import java.net.URI;

import java.util.List;

import jakarta.ws.rs.ServiceUnavailableException;

import gr.gnoome.Domain.Person;
import gr.gnoome.Utility.Database_Manager;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/Civilians")
public class Civilian_Service {

    private static void CheckDatabaseAvailability(){
         if(!Database_Manager.existsDatabase()){

           throw new ServiceUnavailableException(Response.status(Response.Status.SERVICE_UNAVAILABLE)
        .entity("SQL database is unavailable")
        .type(MediaType.TEXT_PLAIN)
        .build());

        }
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addperson(@Context UriInfo uriInfo, Person person) {
       

       CheckDatabaseAvailability();

        if (person == null) {
            throw new BadRequestException("Civilian is empty");
        }

        if (person.getId().length() != 8) {
            throw new BadRequestException("Id is in wrong format");
        }
        if (person.getName().isEmpty()) {
            throw new BadRequestException("Name is in wrong format");
        }
        if (person.getSurname().isEmpty()) {
            throw new BadRequestException("Surname is in wrong format");
        }
        if (!person.getBirthdate().matches("\\d{2}-\\d{2}-\\d{4}")) {
            throw new BadRequestException("Birthdate is in wrong format");
        }
        if (person.getTax() != null && !person.getTax().matches("\\d{9}")) {
            throw new BadRequestException("Tax is in wrong format");
        }

        boolean IsItHere = Database_Manager.existperson(person.getId());
        if (IsItHere) {
            throw new BadRequestException("Civilian already in the database");
        } else {
            Database_Manager.addperson(person);
        }


        URI location = uriInfo.getAbsolutePathBuilder()
                .path(person.getId())
                .build();

        return Response.created(location).entity("Person added with ID: " + person.getId()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getAllCivilians(){

        CheckDatabaseAvailability();
        return Database_Manager.viewallpersons();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getSelectedCivilians(
      @QueryParam("id") String id,
      @QueryParam("name") String name,
      @QueryParam("surname") String surname,
      @QueryParam("birthdate") String birthdate,
      @QueryParam("gender") String gender,
      @QueryParam("address") String address,
      @QueryParam("tax") String tax){

        CheckDatabaseAvailability();

        Person person = new Person();

        person.setid(id);
        person.setName(name);
        person.setSurname(surname);
        person.setGender(gender);
        person.setBirthdate(birthdate);
        person.setAddress(address);
        person.setTax(tax);

        return Database_Manager.ViewSelectedCivilians(person);
    }

   @DELETE
    @Path("/{id}")
   public Response DeleteCivilian(@PathParam("id") String id){

        CheckDatabaseAvailability();
       
        if(Database_Manager.deleteperson(id)){
             return Response.accepted("the person with ID: " + id + " has been successfully deleted").build();
        }
        else{
            throw new NotFoundException();
        }

   }

   @PATCH
   @Path("/{id}")
   public Response UpdateCivilian(@PathParam("id") String id, @QueryParam("address") String address,@QueryParam("tax") String tax){

        CheckDatabaseAvailability();

        

        if(Database_Manager.updateperson(id,address,tax)){

            return Response.ok("the person with ID: " + id + " has been successfully updated").build();
        }
        else{
            throw new BadRequestException();
        }
   }
}

