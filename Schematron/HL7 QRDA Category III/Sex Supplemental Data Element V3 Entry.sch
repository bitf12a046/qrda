<?xml version="1.0" encoding="UTF-8"?>

<sch:schema xmlns:voc="http://www.lantanagroup.com/voc" xmlns:svs="urn:ihe:iti:svs:2008" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sdtc="urn:hl7-org:sdtc" xmlns="urn:hl7-org:v3" xmlns:cda="urn:hl7-org:v3" xmlns:sch="http://purl.oclc.org/dsdl/schematron">
    <sch:ns prefix="voc" uri="http://www.lantanagroup.com/voc" />
    <sch:ns prefix="svs" uri="urn:ihe:iti:svs:2008" />
    <sch:ns prefix="xsi" uri="http://www.w3.org/2001/XMLSchema-instance" />
    <sch:ns prefix="sdtc" uri="urn:hl7-org:sdtc" />
    <sch:ns prefix="cda" uri="urn:hl7-org:v3" />
    
    <sch:phase id="errors">
        <sch:active pattern="Sex_Supplemental_Data_Element_V3-pattern-errors" />
    </sch:phase>
    
    <sch:pattern id="Sex_Supplemental_Data_Element_V3-pattern-errors">
        <sch:rule id="Sex_Supplemental_Data_Element_V3-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.6'][@extension='2016-09-01']]">
            <sch:assert id="a-3259-18230-error" test="@classCode='OBS'">SHALL contain exactly one [1..1] @classCode="OBS" (CodeSystem: HL7ActClass urn:oid:2.16.840.1.113883.5.6 STATIC) (CONF:3259-18230). </sch:assert>
            <sch:assert id="a-3259-18231-error" test="@moodCode='EVN'">SHALL contain exactly one [1..1] @moodCode="EVN" (CodeSystem: ActMood urn:oid:2.16.840.1.113883.5.1001 STATIC) (CONF:3259-18231). </sch:assert>
            <sch:assert id="a-3259-18232-error" test="count(cda:templateId[@root='2.16.840.1.113883.10.20.27.3.6'][@extension='2016-09-01'])=1">SHALL contain exactly one [1..1] templateId (CONF:3259-18232) such that it SHALL contain exactly one [1..1] @root="2.16.840.1.113883.10.20.27.3.6" (CONF:3259-18233). SHALL contain exactly one [1..1] @extension="2016-09-01" (CONF:3259-21160).</sch:assert>
            <sch:assert id="a-3259-18234-error" test="count(cda:code)=1">SHALL contain exactly one [1..1] code (CONF:3259-18234). </sch:assert>
            <sch:assert id="a-3259-18124-error" test="count(cda:statusCode)=1">SHALL contain exactly one [1..1] statusCode (CONF:3259-18124). </sch:assert>
            <sch:assert id="a-3259-18236-error" test="count(cda:value[@xsi:type='CD'])=1">SHALL contain exactly one [1..1] value with @xsi:type="CD", where the code SHALL be selected from ValueSet ONC Administrative Sex urn:oid:2.16.840.1.113762.1.4.1 DYNAMIC (CONF:3259-18236). </sch:assert>
            <sch:assert id="a-3259-18126-error" test="count(cda:entryRelationship[@typeCode='SUBJ'][@inversionInd='true'][count(cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.3']])=1])=1">SHALL contain exactly one [1..1] entryRelationship (CONF:3259-18126) such that it SHALL contain exactly one [1..1] @typeCode="SUBJ" Has Subject (CodeSystem: HL7ActRelationshipType urn:oid:2.16.840.1.113883.5.1002 STATIC) (CONF:3259-18127). SHALL contain exactly one [1..1] @inversionInd="true" (CONF:3259-18128). SHALL contain exactly one [1..1] Aggregate Count (identifier: urn:oid:2.16.840.1.113883.10.20.27.3.3) (CONF:3259-18129).</sch:assert>
        </sch:rule>
        <sch:rule id="Sex_Supplemental_Data_Element_V3-code-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.6'][@extension='2016-09-01']]/cda:code">
            <sch:assert id="a-3259-18235-error" test="@code='76689-9'">This code SHALL contain exactly one [1..1] @code="76689-9" Sex assigned at birth (CONF:3259-18235).</sch:assert>
            <sch:assert id="a-3259-21163-error" test="@codeSystem='2.16.840.1.113883.6.1'">This code SHALL contain exactly one [1..1] @codeSystem="2.16.840.1.113883.6.1" (CodeSystem: LOINC urn:oid:2.16.840.1.113883.6.1 STATIC) (CONF:3259-21163).</sch:assert>
        </sch:rule>
        <sch:rule id="Sex_Supplemental_Data_Element_V3-statusCode-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.6'][@extension='2016-09-01']]/cda:statusCode">
            <sch:assert id="a-3259-18125-error" test="@code='completed'">This statusCode SHALL contain exactly one [1..1] @code="completed" Completed (CodeSystem: ActStatus urn:oid:2.16.840.1.113883.5.14 STATIC) (CONF:3259-18125).</sch:assert>
        </sch:rule>
     </sch:pattern>
</sch:schema>