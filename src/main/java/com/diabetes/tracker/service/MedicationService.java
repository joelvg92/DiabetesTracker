package com.diabetes.tracker.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicationService {

    private static String baseUrl="http://ec2-52-15-132-164.us-east-2.compute.amazonaws.com/fhir";
    IGenericClient client = null;
    FhirContext ctx = null;
    public MedicationService(){
        ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient(baseUrl);
    }

    public String addMedication(String patientId,String name,String dosage,String unit,String dateTime){
        MedicationAdministration medicationAdministration = new MedicationAdministration();
        Bundle resp = null;
        try {
            Reference r1 = new Reference();
            r1.setReference("Patient/"+patientId);
            r1.setDisplay(patientId);
            medicationAdministration.setSubject(r1);
            MedicationAdministration.MedicationAdministrationDosageComponent medicationAdministrationDosageComponent = new MedicationAdministration.MedicationAdministrationDosageComponent();
            medicationAdministrationDosageComponent.setText(name);
            Quantity q = new Quantity();
            q.setCode(dosage);
            q.setSystem("Diabetic Tracker");
            q.setUnit(unit);
            medicationAdministrationDosageComponent.setDose(q);
            medicationAdministration.setDosage(medicationAdministrationDosageComponent);
            Annotation annotation = new Annotation();
            annotation.setTimeElement(DateTimeType.now());
            annotation.setText(dateTime);
            List<Annotation> annotationList = new ArrayList<>();
            annotationList.add(annotation);
            medicationAdministration.setNote(annotationList);

            Bundle bundle = new Bundle();
            bundle.setType(Bundle.BundleType.TRANSACTION);

            bundle.addEntry()
                    .setFullUrl(medicationAdministration.getIdElement().getValue())
                    .setResource(medicationAdministration)
                    .getRequest()
                    .setUrl("MedicationAdministration")
                    .setMethod(Bundle.HTTPVerb.POST);

            resp = client.transaction().withBundle(bundle).execute();
            System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
        } catch (ResourceNotFoundException e) {
            return null;
        }
        String id = resp.getEntry().get(0).getResponse().getLocationElement().getValueAsString().split("/")[1];
        return id;
    }

    public String updateMedication(String id,String patientId,String name,String dosage,String unit,String dateTime){
        try {
            MedicationAdministration medicationAdministration = new MedicationAdministration();
            medicationAdministration.setId(id);
            Reference r1 = new Reference();
            r1.setReference("Patient/" + patientId);
            r1.setDisplay(patientId);
            medicationAdministration.setSubject(r1);
            MedicationAdministration.MedicationAdministrationDosageComponent medicationAdministrationDosageComponent = new MedicationAdministration.MedicationAdministrationDosageComponent();
            medicationAdministrationDosageComponent.setText(name);
            Quantity q = new Quantity();
            q.setCode(dosage);
            q.setSystem("Diabetic Tracker");
            q.setUnit(unit);
            medicationAdministrationDosageComponent.setDose(q);
            medicationAdministration.setDosage(medicationAdministrationDosageComponent);
            Annotation annotation = new Annotation();
            annotation.setTimeElement(DateTimeType.now());
            annotation.setText(dateTime);
            List<Annotation> annotationList = new ArrayList<>();
            annotationList.add(annotation);
            medicationAdministration.setNote(annotationList);
            MethodOutcome outcome = client.update()
                    .resource(medicationAdministration)
                    .execute();
            return outcome.getId().getValue();
        }catch (Exception e){
            return null;
        }
    }

    public List<MedicationAdministration> getMedicationByUser(String user){
        Bundle bundle = client.search().forResource(MedicationAdministration.class)
                .where(MedicationAdministration.SUBJECT.hasId(user))
                .returnBundle(Bundle.class).execute();
        ArrayList<MedicationAdministration> mList = new ArrayList<MedicationAdministration>();
        mList.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, MedicationAdministration.class));
        while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            mList.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, MedicationAdministration.class));
        }
        return mList;
    }

    public boolean deleteMedicationBy(String id){
        client.delete().resourceById(new IdType("MedicationAdministration", id)).execute();
        return true;
    }



    public static void main(String [] args){
        MedicationService medicationService = new MedicationService();
       // String id = medicationService.updateMedication("264","262","metropolololo","100","mg");
        List<MedicationAdministration> mList = medicationService.getMedicationByUser("262");
        for(MedicationAdministration m:mList) {
            System.out.println(m.getId());
        }
        medicationService.deleteMedicationBy("264");
    }

}
