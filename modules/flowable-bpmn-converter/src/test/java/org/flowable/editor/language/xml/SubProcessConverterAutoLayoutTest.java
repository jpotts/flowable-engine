package org.flowable.editor.language.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.UserTask;
import org.flowable.bpmn.model.ValuedDataObject;
import org.junit.Test;

public class SubProcessConverterAutoLayoutTest extends AbstractConverterTest {

    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFile();

        // Add DI information to bpmn model
        BpmnAutoLayout bpmnAutoLayout = new BpmnAutoLayout(bpmnModel);
        bpmnAutoLayout.execute();

        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
        deployProcess(parsedModel);
    }

    protected String getResource() {
        return "subprocessmodel_autolayout.bpmn";
    }

    private void validateModel(BpmnModel model) {
        FlowElement flowElement = model.getMainProcess().getFlowElement("start1");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof StartEvent);
        assertEquals("start1", flowElement.getId());

        flowElement = model.getMainProcess().getFlowElement("userTask1");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof UserTask);
        assertEquals("userTask1", flowElement.getId());
        UserTask userTask = (UserTask) flowElement;
        assertEquals(1, userTask.getCandidateUsers().size());
        assertEquals(1, userTask.getCandidateGroups().size());

        flowElement = model.getMainProcess().getFlowElement("subprocess1");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof SubProcess);
        assertEquals("subprocess1", flowElement.getId());
        SubProcess subProcess = (SubProcess) flowElement;
        assertEquals(6, subProcess.getFlowElements().size());

        List<ValuedDataObject> dataObjects = ((SubProcess) flowElement).getDataObjects();
        assertEquals(1, dataObjects.size());

        ValuedDataObject dataObj = dataObjects.get(0);
        assertEquals("SubTest", dataObj.getName());
        assertEquals("xsd:string", dataObj.getItemSubjectRef().getStructureRef());
        assertTrue(dataObj.getValue() instanceof String);
        assertEquals("Testing", dataObj.getValue());
    }
}
