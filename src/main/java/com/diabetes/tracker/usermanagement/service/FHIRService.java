package com.diabetes.tracker.usermanagement.service;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Service
public class FHIRService {
    private static String baseUrl="http://ec2-52-15-132-164.us-east-2.compute.amazonaws.com/fhir";
    IGenericClient client = null;
    FhirContext ctx = null;
    public FHIRService(){
        ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient(baseUrl);
    }

    public String createPatientFirebase(String name,String gender, String dob) {
        Patient patient = new Patient();
        Bundle resp = null;
        try {
            String [] names = name.split(" ");
            patient.addIdentifier()
                    .setSystem("DiabetesTracker")
                    .setValue(names[0]+':'+dob.replaceAll("-",""));
            patient.addName()
                    .setFamily(names[1])
                    .addGiven(names[0])
                    .addGiven(names[1]);
            if(gender.contentEquals("male")){
                patient.setGender(Enumerations.AdministrativeGender.MALE);
            }else{
                patient.setGender(Enumerations.AdministrativeGender.FEMALE);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            Date date = formatter.parse(dob);
            patient.setBirthDate(date);

            Bundle bundle = new Bundle();
            bundle.setType(Bundle.BundleType.TRANSACTION);

            bundle.addEntry()
                    .setFullUrl(patient.getIdElement().getValue())
                    .setResource(patient)
                    .getRequest()
                    .setUrl("Patient")
                    .setMethod(Bundle.HTTPVerb.POST);

            // Log the request
           // FhirContext ctx = FhirContext.forR4();
            //System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));

            resp = client.transaction().withBundle(bundle).execute();
            //System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
        } catch (ResourceNotFoundException | ParseException e) {
            return null;
        }
        String id = resp.getEntry().get(0).getResponse().getLocationElement().getValueAsString().split("/")[1];
        return id;

    }

    public static void main(String [] args){
        FHIRService service = new FHIRService();
        String id = service.createPatientFirebase("John Doe3","male","1995-10-25");
        System.out.println(id);
    }

}
