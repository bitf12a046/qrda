<?xml version="1.0" encoding="UTF-8"?>

<sch:schema xmlns:voc="http://www.lantanagroup.com/voc" xmlns:svs="urn:ihe:iti:svs:2008" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sdtc="urn:hl7-org:sdtc" xmlns="urn:hl7-org:v3" xmlns:cda="urn:hl7-org:v3" xmlns:sch="http://purl.oclc.org/dsdl/schematron">
    <sch:ns prefix="voc" uri="http://www.lantanagroup.com/voc" />
    <sch:ns prefix="svs" uri="urn:ihe:iti:svs:2008" />
    <sch:ns prefix="xsi" uri="http://www.w3.org/2001/XMLSchema-instance" />
    <sch:ns prefix="sdtc" uri="urn:hl7-org:sdtc" />
    <sch:ns prefix="cda" uri="urn:hl7-org:v3" />
    
    <sch:phase id="errors">
        <sch:active pattern="Measure_Performed-pattern-errors" />
    </sch:phase>
    
    <sch:pattern id="Measure_Performed-pattern-errors">
        <sch:rule id="Measure_Performed-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.27'][@extension='2016-09-01']]">
            <sch:assert id="a-3259-21221-error" test="@classCode='OBS'">SHALL contain exactly one [1..1] @classCode="OBS" (CodeSystem: HL7ActClass urn:oid:2.16.840.1.113883.5.6 STATIC) (CONF:3259-21221).</sch:assert>
            <sch:assert id="a-3259-21222-error" test="@moodCode='EVN'">SHALL contain exactly one [1..1] @moodCode="EVN" (CodeSystem: ActMood urn:oid:2.16.840.1.113883.5.1001 STATIC) (CONF:3259-21222).</sch:assert>
            <sch:assert id="a-3259-21185-error" test="count(cda:templateId[@root='2.16.840.1.113883.10.20.27.3.27'][@extension='2016-09-01'])=1">SHALL contain exactly one [1..1] templateId (CONF:3259-21185) such that it SHALL contain exactly one [1..1] @root="2.16.840.1.113883.10.20.27.3.27" (CONF:3259-21203). SHALL contain exactly one [1..1] @extension="2016-09-01" (CONF:3259-21399).</sch:assert>
            <sch:assert id="a-3259-21382-error" test="count(cda:code)=1">SHALL contain exactly one [1..1] code (CONF:3259-21382).</sch:assert>
            <sch:assert id="a-3259-21440-error" test="count(cda:statusCode)=1">SHALL contain exactly one [1..1] statusCode (CodeSystem: ActStatus urn:oid:2.16.840.1.113883.5.14) (CONF:3259-21440).</sch:assert>
            <sch:assert id="a-3259-21391-error" test="count(cda:value[@xsi:type='CD'])=1">SHALL contain exactly one [1..1] value with @xsi:type="CD", where the code SHALL be selected from ValueSet Yes No Indicator (HL7) urn:oid:2.16.840.1.114222.4.11.819 DYNAMIC (CONF:3259-21391).</sch:assert>
        </sch:rule>
        <sch:rule id="Measure_Performed-code-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.27'][@extension='2016-09-01']]/cda:code">
            <sch:assert id="a-3259-21392-error" test="@code='ASSERTION'">This code SHALL contain exactly one [1..1] @code="ASSERTION" Assertion (CONF:3259-21392).</sch:assert>
            <sch:assert id="a-3259-21393-error" test="@codeSystem='2.16.840.1.113883.5.4'">This code SHALL contain exactly one [1..1] @codeSystem="2.16.840.1.113883.5.4" (CodeSystem: ActCode urn:oid:2.16.840.1.113883.5.4) (CONF:3259-21393).</sch:assert>
        </sch:rule>
        <sch:rule id="Measure_Performed-statusCode-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.27.3.27'][@extension='2016-09-01']]/cda:statusCode">
            <sch:assert id="a-3259-21442-error" test="@code='completed'">This statusCode SHALL contain exactly one [1..1] @code="completed" Completed (CONF:3259-21442).</sch:assert>
        </sch:rule>
    </sch:pattern>
</sch:schema>