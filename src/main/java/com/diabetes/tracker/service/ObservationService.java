package com.diabetes.tracker.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import com.diabetes.tracker.model.ResultSetObservation;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObservationService {
    private static String baseUrl="http://ec2-52-15-132-164.us-east-2.compute.amazonaws.com/fhir";
    IGenericClient client = null;
    FhirContext ctx = null;
    public ObservationService(){
        ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient(baseUrl);
    }
    public String addObservation(String patientId,String reading,String description,String time){
        Observation observation = new Observation();
        Bundle resp = null;
        try {
            Reference r1 = new Reference();
            r1.setReference("Patient/"+patientId);
            r1.setDisplay(patientId);
            observation.setSubject(r1);
            Quantity q = new Quantity();
            q.setCode(reading);
            q.setSystem("Diabetic Tracker");
            q.setUnit(description);
            observation.setValue(q);
            observation.setStatus(Observation.ObservationStatus.FINAL);
            Annotation annotation = new Annotation();
            annotation.setText(LocalDate.now()+":"+time);
            List<Annotation> annotationList = new ArrayList<>();
            annotationList.add(annotation);
            observation.setNote(annotationList);

            Bundle bundle = new Bundle();
            bundle.setType(Bundle.BundleType.TRANSACTION);

            bundle.addEntry()
                    .setFullUrl(observation.getIdElement().getValue())
                    .setResource(observation)
                    .getRequest()
                    .setUrl("Observation")
                    .setMethod(Bundle.HTTPVerb.POST);

            resp = client.transaction().withBundle(bundle).execute();
            System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
        } catch (ResourceNotFoundException e) {
            return null;
        }
        String id = resp.getEntry().get(0).getResponse().getLocationElement().getValueAsString().split("/")[1];
        return id;
    }

    public String updateObservation(String id,String patientId,String reading,String description,String time){
        try {
            Observation observation = new Observation();
            observation.setId(id);
            Reference r1 = new Reference();
            r1.setReference("Patient/"+patientId);
            r1.setDisplay(patientId);
            observation.setSubject(r1);
            Quantity q = new Quantity();
            q.setCode(reading);
            q.setSystem("Diabetic Tracker");
            q.setUnit(description);
            observation.setValue(q);
            observation.setStatus(Observation.ObservationStatus.FINAL);
            Annotation annotation = new Annotation();
            annotation.setText(LocalDate.now()+":"+time);
            List<Annotation> annotationList = new ArrayList<>();
            annotationList.add(annotation);
            observation.setNote(annotationList);
            MethodOutcome outcome = client.update()
                    .resource(observation)
                    .execute();
            return outcome.getId().getValue();
        }catch (Exception e){
            return null;
        }
    }

    public List<ResultSetObservation> getObservationByUser(String user){
        List<ResultSetObservation> resultSetObservationList = new ArrayList<>();
        Bundle bundle = client.search().forResource(Observation.class)
                .where(Observation.SUBJECT.hasId(user))
                .returnBundle(Bundle.class).execute();
        ArrayList<Observation> mList = new ArrayList<Observation>();
        mList.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, Observation.class));
        while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            mList.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, Observation.class));
        }
        for(Observation observation: mList){
            ResultSetObservation resultSetObservation = new ResultSetObservation();
            resultSetObservation.setId(observation.getId());
            resultSetObservation.setDiabeticRecord(observation.getValueQuantity().getCode());
            resultSetObservation.setMeal(observation.getValueQuantity().getUnit());
            resultSetObservation.setTime(observation.getNote().get(0).getText());
            resultSetObservationList.add(resultSetObservation);
        }
        return resultSetObservationList;
    }

    public boolean deleteObservationBy(String id){
        client.delete().resourceById(new IdType("Observation", id)).execute();
        return true;
    }



/*    public static void main(String [] args){
        ObservationService observationService = new ObservationService();
        String id = observationService.addObservation("262","Before meal","180","mg","Nov 29,10:30am");
        observationService.updateObservation(id,"262","metropolololo","100","mg","Nov 29,10:30am");
        List<Observation> mList = observationService.getObservationByUser(id);
        for(Observation m:mList) {
            System.out.println(m.getId());
        }
        //observationService.deleteObservationBy(id);
    }*/
}
