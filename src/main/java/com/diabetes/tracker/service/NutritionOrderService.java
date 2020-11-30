package com.diabetes.tracker.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import com.diabetes.tracker.model.ResultSetNutritionOrder;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NutritionOrderService {
    private static String baseUrl="http://ec2-52-15-132-164.us-east-2.compute.amazonaws.com/fhir";
    IGenericClient client = null;
    FhirContext ctx = null;
    public NutritionOrderService(){
        ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient(baseUrl);
    }
    public String addNutritionOrder(String patientId,String name,String dosage,String unit,String dateTime){
        NutritionOrder nutritionOrder = new NutritionOrder();
        Bundle resp = null;
        try {
            Reference r1 = new Reference();
            r1.setReference("Patient/"+patientId);
            r1.setDisplay(patientId);
            nutritionOrder.setPatient(r1);
            CodeableConcept codeableConcept = new CodeableConcept();
            Coding coding = new Coding();
            coding.setCode(dosage+":"+unit);
            coding.setDisplay(name);
            coding.setSystem("Diabetic Tracker");
            List<Coding> codingList = new ArrayList<>();
            codingList.add(coding);
            codeableConcept.setCoding(codingList);
            List<CodeableConcept> codeableConceptList = new ArrayList<>();
            codeableConceptList.add(codeableConcept);
            nutritionOrder.setFoodPreferenceModifier(codeableConceptList);
            nutritionOrder.setStatus(NutritionOrder.NutritionOrderStatus.ACTIVE);
            Annotation annotation = new Annotation();
            annotation.setText(name);
            annotation.setText(dateTime);
            annotation.setTimeElement(DateTimeType.now());
            List<Annotation> annotationList = new ArrayList<>();
            annotationList.add(annotation);
            nutritionOrder.setNote(annotationList);

            Bundle bundle = new Bundle();
            bundle.setType(Bundle.BundleType.TRANSACTION);

            bundle.addEntry()
                    .setFullUrl(nutritionOrder.getIdElement().getValue())
                    .setResource(nutritionOrder)
                    .getRequest()
                    .setUrl("NutritionOrder")
                    .setMethod(Bundle.HTTPVerb.POST);

            resp = client.transaction().withBundle(bundle).execute();
            System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
        } catch (ResourceNotFoundException e) {
            return null;
        }
        String id = resp.getEntry().get(0).getResponse().getLocationElement().getValueAsString().split("/")[1];
        return id;
    }

    public String updateNutritionOrder(String id,String patientId,String name,String dosage,String unit,String dateTime){
        NutritionOrder nutritionOrder = new NutritionOrder();
        try {
            nutritionOrder.setId(id);
            Reference r1 = new Reference();
            r1.setReference("Patient/"+patientId);
            r1.setDisplay(patientId);
            nutritionOrder.setPatient(r1);
            CodeableConcept codeableConcept = new CodeableConcept();
            Coding coding = new Coding();
            coding.setCode(dosage+":"+unit);
            coding.setDisplay(name);
            coding.setSystem("Diabetic Tracker");
            List<Coding> codingList = new ArrayList<>();
            codingList.add(coding);
            codeableConcept.setCoding(codingList);
            List<CodeableConcept> codeableConceptList = new ArrayList<>();
            codeableConceptList.add(codeableConcept);
            nutritionOrder.setFoodPreferenceModifier(codeableConceptList);
            nutritionOrder.setStatus(NutritionOrder.NutritionOrderStatus.ACTIVE);
            Annotation annotation = new Annotation();
            annotation.setText(dateTime);
            annotation.setTimeElement(DateTimeType.now());
            List<Annotation> annotationList = new ArrayList<>();
            annotationList.add(annotation);
            nutritionOrder.setNote(annotationList);
            MethodOutcome outcome = client.update()
                    .resource(nutritionOrder)
                    .execute();
            return outcome.getId().getValue();
        }catch (Exception e){
            return null;
        }
    }

    public List<ResultSetNutritionOrder> getNutritionOrderByUser(String user){
        List<ResultSetNutritionOrder> resultSetNutritionOrderList = new ArrayList<>();
        Bundle bundle = client.search().forResource(NutritionOrder.class)
                .where(NutritionOrder.PATIENT.hasId(user))
                .returnBundle(Bundle.class).execute();
        ArrayList<NutritionOrder> mList = new ArrayList<NutritionOrder>();
        mList.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, NutritionOrder.class));
        while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            mList.addAll(BundleUtil.toListOfResourcesOfType(client.getFhirContext(), bundle, NutritionOrder.class));
        }
        for(NutritionOrder nutritionOrder: mList){
            ResultSetNutritionOrder resultSetNutritionOrder = new ResultSetNutritionOrder();
            resultSetNutritionOrder.setId(nutritionOrder.getId());
            resultSetNutritionOrder.setFoodName(nutritionOrder.getFoodPreferenceModifier().get(0).getCoding().get(0).getDisplay());
            String val =nutritionOrder.getFoodPreferenceModifier().get(0).getCoding().get(0).getCode();
            String splitVal[] = val.split(":");
            resultSetNutritionOrder.setMealSize(splitVal[0]);
            resultSetNutritionOrder.setCalories(Integer.toString(Integer.parseInt(splitVal[3]) * Integer.parseInt(splitVal[0])));
            Date dt =nutritionOrder.getNote().get(0).getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String strDate = dateFormat.format(dt);
            resultSetNutritionOrder.setTime(strDate+" "+nutritionOrder.getNote().get(0).getText());
            resultSetNutritionOrderList.add(resultSetNutritionOrder);
        }
        return resultSetNutritionOrderList;
    }

    public boolean deleteNutritionOrderBy(String id){
        client.delete().resourceById(new IdType("NutritionOrder", id)).execute();
        return true;
    }



/*    public static void main(String [] args){
        NutritionOrderService nutritionOrderService = new NutritionOrderService();
        String id = nutritionOrderService.addNutritionOrder("262","Burger","180","mg","Nov 29,10:30am");
        nutritionOrderService.updateNutritionOrder(id,"262","Burger 2","100","mg","Nov 29,10:30am");
        List<NutritionOrder> mList = nutritionOrderService.getNutritionOrderByUser(id);
        for(NutritionOrder m:mList) {
            System.out.println(m.getId());
        }
        //nutritionOrderService.deleteNutritionOrderBy(id);
    }*/
}
