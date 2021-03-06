<?xml version="1.0" encoding="UTF-8"?>

<sch:schema xmlns:voc="http://www.lantanagroup.com/voc" xmlns:svs="urn:ihe:iti:svs:2008" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:sdtc="urn:hl7-org:sdtc" xmlns="urn:hl7-org:v3" xmlns:cda="urn:hl7-org:v3" xmlns:sch="http://purl.oclc.org/dsdl/schematron">
    <sch:ns prefix="voc" uri="http://www.lantanagroup.com/voc" />
    <sch:ns prefix="svs" uri="urn:ihe:iti:svs:2008" />
    <sch:ns prefix="xsi" uri="http://www.w3.org/2001/XMLSchema-instance" />
    <sch:ns prefix="sdtc" uri="urn:hl7-org:sdtc" />
    <sch:ns prefix="cda" uri="urn:hl7-org:v3" />
    
    <sch:phase id="errors">
        <sch:active pattern="Provider-Care-Experience-pattern-errors" />
    </sch:phase>
    
    <sch:pattern id="Provider-Care-Experience-pattern-errors">
        <sch:rule id="Provider-Care-Experience-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.24.3.67'][@extension='2017-08-01']]">
		  <sch:assert id="a-3343-12479-error" test="@classCode='OBS'">SHALL contain exactly one [1..1] @classCode="OBS" observation, which SHALL be selected from CodeSystem HL7ActClass (urn:oid:2.16.840.1.113883.5.6) (CONF:3343-12479).</sch:assert>
		  <sch:assert id="a-3343-12480-error" test="@moodCode='EVN'">SHALL contain exactly one [1..1] @moodCode="EVN" event, which SHALL be selected from CodeSystem ActMood (urn:oid:2.16.840.1.113883.5.1001) (CONF:3343-12480).</sch:assert>
		  <sch:assert id="a-3343-28100-error" test="not(@negationInd)">SHALL NOT contain [0..0] @negationInd (CONF:3343-28100).</sch:assert>
            <sch:assert id="a-3343-12481-error" test="count(cda:templateId[@root='2.16.840.1.113883.10.20.24.3.67'][@extension='2017-08-01'])=1">SHALL contain exactly one [1..1] templateId (CONF:3343-12481) such that it SHALL contain exactly one [1..1] @root="2.16.840.1.113883.10.20.24.3.67" (CONF:3343-12482). SHALL contain exactly one [1..1] @extension="2017-08-01" (CONF:3343-27293).</sch:assert>
		  <sch:assert id="a-3343-12484-error" test="count(cda:id) &gt;= 1">SHALL contain at least one [1..*] id (CONF:3343-12484).</sch:assert>
		  <sch:assert id="a-3343-12485-error" test="count(cda:code)=1">SHALL contain exactly one [1..1] code (CONF:3343-12485).</sch:assert>
		  <sch:assert id="a-3343-12486-error" test="count(cda:statusCode[@code='completed'])=1">SHALL contain exactly one [1..1] statusCode="completed", which SHALL be selected from CodeSystem ActStatus (urn:oid:2.16.840.1.113883.5.14) (CONF:3343-12486).</sch:assert>
		  <sch:assert id="a-3343-12572-error" test="count(cda:value[@xsi:type='CD'])=1">SHALL contain exactly one [1..1] value with @xsi:type="CD" (CONF:3343-12572).</sch:assert>
          <sch:assert id="a-3343-28941-error" test="count(cda:author)=1">SHALL contain exactly one [1..1] author (CONF:3343-28941).</sch:assert>
        </sch:rule>        
        <sch:rule id="Provider-Care-Experience-code-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.24.3.67'][@extension='2017-08-01']]/cda:code">
		  <sch:assert id="a-3343-27562-error" test="@code='77219-4'">This code SHALL contain exactly one [1..1] @code="77219-4" Provider satisfaction with healthcare delivery (CONF:3343-27562).</sch:assert>
		  <sch:assert id="a-3343-27563-error" test="@codeSystem='2.16.840.1.113883.6.1'">This code SHALL contain exactly one [1..1] @codeSystem="2.16.840.1.113883.6.1" (CodeSystem: LOINC urn:oid:2.16.840.1.113883.6.1) (CONF:3343-27563).</sch:assert>
       </sch:rule>               
        <sch:rule id="Provider-Care-Experience-author-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.24.3.67'][@extension='2017-08-01']]/cda:author">
            <sch:assert id="a-3343-29077-error" test="count(cda:time)=1">This author SHALL contain exactly one [1..1] time (CONF:3343-29077). </sch:assert>
            <sch:assert id="a-3343-29076-error" test="count(cda:assignedAuthor)=1">This author SHALL contain exactly one [1..1] assignedAuthor (CONF:3343-29076). </sch:assert>
        </sch:rule> 
        
        <sch:rule id="Provider-Care-Experience-author-assignedAuthor-errors" context="cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.24.3.67'][@extension='2017-08-01']]/cda:author/cda:assignedAuthor">
            <sch:assert id="a-3343-29078-warning" test="count(cda:id)&gt;=1">This assignedAuthor SHALL contain at least one [1..*] id (CONF:3343-29078).</sch:assert>
        </sch:rule> 
        
      </sch:pattern>
    
</sch:schema>