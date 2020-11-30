package com.diabetes.tracker.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import com.diabetes.tracker.model.ResultSetMedicaiton;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            q.setCode(dosage+" "+unit);
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

    public List<ResultSetMedicaiton> getMedicationByUser(String user){
        List<ResultSetMedicaiton>  medications = new ArrayList<>();
        Bundle bundle = client.search().forResource(MedicationAdministration.class)
                .where(MedicationAdministration.SUBJECT.hasId(user))
                .returnBundle(Bundle.class).execute();
        List<MedicationAdministration> medicationAdmins = new ArrayList<>();
        medicationAdmins.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, MedicationAdministration.class));
        while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            medicationAdmins.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, MedicationAdministration.class));
        }
        for(MedicationAdministration medicationAdministration: medicationAdmins){
            ResultSetMedicaiton resultSetMedicaiton = new ResultSetMedicaiton();
            resultSetMedicaiton.setId(medicationAdministration.getId());
            resultSetMedicaiton.setMedicationName(medicationAdministration.getDosage().getText());
            Date dt =medicationAdministration.getNote().get(0).getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = dateFormat.format(dt);
            resultSetMedicaiton.setTime(strDate+":"+medicationAdministration.getNote().get(0).getText());
            resultSetMedicaiton.setUnit(medicationAdministration.getDosage().getDose().getUnit());
            resultSetMedicaiton.setDosage(medicationAdministration.getDosage().getDose().getCode());
            medications.add(resultSetMedicaiton);
        }
        return medications;
    }

    public boolean deleteMedicationBy(String id){
        client.delete().resourceById(new IdType("MedicationAdministration", id)).execute();
        return true;
    }


}
