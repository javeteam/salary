<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>Result</title>
        <jsp:include page="assets.jsp"/>
    </head>

    <body>
        <div class="container">
            <div class="py-5 text-center">
                <h2>To-Do List</h2>
            </div>
            <p class="lead"></p>

            <div class="row">
                <div class="col-md-12 h5">
                    <c:choose>
                        <c:when test="${param.requestType == '0' || param.requestType == '1'}">                       
                            <c:if test="${param.requestType == '0'}">
                           		<c:choose>
                            		<c:when test="${param.LangCombinationDoable == 'yes' && param.ServiceDoable == 'yes' && param.DomainDoable == 'yes'}">
                            			<c:choose>
     										<c:when test="${param.MainCatTool == 'yes' && param.DeadlineFeasible == 'yes'}">
        										<div><i class="fas fa-check-circle"></i> Accept the project</div>
   											 </c:when>
    										<c:otherwise>
         										<div><i class="fas fa-exclamation-circle"></i> Consult with your Team Lead before ACCEPTING the project</div>
   									 		</c:otherwise>
										</c:choose>
                            		</c:when>
                            		<c:otherwise>
                            			<div><i class="fas fa-times-circle"></i> Consult with your Team Lead before REJECTING the project</div>
                       			 	</c:otherwise>
                       		 	</c:choose>
                       		 </c:if>
                       		 <c:if test="${param.requestType == '1'}">
                       		 	<c:choose>
     								<c:when test="${param.ProjectDoable == 'yes'}">
     									<c:if test="${param.ServiceType == '9' || param.ServiceType == '11'}">
     										<div class="custom-control custom-checkbox">
                                        		<input type="checkbox" class="custom-control-input" id="checkbox-1">
                                        		<label class="custom-control-label" for="checkbox-1">Consult with your Team Lead regarding to requested service</label>
                                    		</div>
     									</c:if>
        								<div><i class="fas fa-envelope"></i> Write an Email: *</div>
        								<div class="mx-5 mt-2 my-3">
        									<c:if test="${param.ServiceType == '0'}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-2">
                                        			<label class="custom-control-label" for="checkbox-2">- to ask why the customer chose Translation Only service and explain potential risks</label>
                                    			</div>
        									</c:if>
        									<c:if test="${empty param.InstrClear}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-3">
                                        			<label class="custom-control-label" for="checkbox-3">- to clarify all unclear or dubious instructions</label>
                                    			</div>
        									</c:if>
        									<c:if test="${empty param.RT1DeadlineFeasible}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-4">
                                        			<label class="custom-control-label" for="checkbox-4">
                                        				<span>- to offer the following options regarding the deadline:</span>
                                        				<div class="mx-5 mt-2 h6">to ask for a deadline extension for the whole project, stating the earliest deadline possible for us <b>(preferred option)</b>;</div>
                                        				<div class="mx-5 h6">to offer to perform a part of the project within the given deadline;</div>
                                        				<div class="mx-5 h6">
                                        					<span>to offer to reduce the workflow step to perform the whole project within the given deadline <b>(not recommended)</b></span>
                                        					<span data-toggle="tooltip" data-html="true" title="ATTENTION! if the client approves the 3d option, you should confirm with the client the possible risks."><i class="fas fa-exclamation-triangle"></i></span>
                                        				</div>
                                        			</label>
                                        		</div>
        									</c:if>
        									<c:if test="${param.ResultUsage == 'yes'}">
        										<c:if test="${param.ServiceType == '0' || param.ServiceType == '3'}">
        											<div class="custom-control custom-checkbox">
                                        				<input type="checkbox" class="custom-control-input" id="checkbox-5">
                                        				<label class="custom-control-label" for="checkbox-5">- to explain potential risks due to selected combination of service and text usage</label>
                                    				</div>
        										</c:if>
        										<c:if test="${param.ServiceType == '1' || param.ServiceType == '4'}">
        											<div class="custom-control custom-checkbox">
                                        				<input type="checkbox" class="custom-control-input" id="checkbox-6">
                                        				<label class="custom-control-label" for="checkbox-6">- to offer to add Review step</label>
                                    				</div>
        										</c:if>
        									</c:if>
        									<c:if test="${param.ResultUsage == 'not-clear'}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-7">
                                        			<label class="custom-control-label" for="checkbox-7">- to ask about the usage of the translated files</label>
                                    			</div>
        									</c:if>
        									<c:if test="${param.ContentType == '1' && empty param.ContentTypeUiUa }">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-8">
                                        			<label class="custom-control-label" for="checkbox-8">- to explain the customer that this work is more time-consuming than the common translation of the connected text, that  and that is why it is charged per hour</label>
                                    			</div>
        									</c:if>
        									<c:if test="${param.ContentType == '2' && empty param.ContentTypeList && param.ServiceType != '10'}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-9">
                                        			<label class="custom-control-label" for="checkbox-9">- to explain the customer that this work is more time-consuming than the common translation of the connected text, that  and that is why it is charged per hour</label>
                                    			</div>
        									</c:if>
        									<c:if test="${param.ContentType == '2' && empty param.ContentTypeList && param.ServiceType != '10'}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-10">
                                        			<label class="custom-control-label" for="checkbox-10">- to explain the customer that this work is more time-consuming than the common translation of the connected text, that  and that is why it is charged per hour</label>
                                    			</div>
        									</c:if>
        									<c:if test="${empty param.PriceCorrect}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-11">
                                        			<label class="custom-control-label" for="checkbox-11">- to ask why the price per word/hour differs from the common one, indicate our agreed price</label>
                                    			</div>
        									</c:if>
        									<c:if test="${empty param.CatCorrect}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-12">
                                        			<label class="custom-control-label" for="checkbox-12">- to ask why the CAT grid differs from the common one, indicate our agreed CAT grid</label>
                                    			</div>
        									</c:if>
        									<c:if test="${empty param.StatisticsCorrect}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-13">
                                        			<label class="custom-control-label" for="checkbox-13">- to point out that the CAT tool statistics differs from the statistics of the project request, ask to advise how we have to proceed</label>
                                    			</div>
        									</c:if>
        									<c:if test="${not empty param.MatchesActionsRequired && param.MatchesPaid == 'no'}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-14">
                                        			<label class="custom-control-label" for="checkbox-14">- to explain to the customer that if we check the Matches/Repetitions, we will have to apply the fee for this work, ask to advise how we have to proceed</label>
                                    			</div>
        									</c:if>
        									<c:if test="${param.AllStepsPaid == 'no'}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-14">
                                        			<label class="custom-control-label" for="checkbox-14">- to point out that the project needs to add the additional step that is not included in the current price, explain why it is necessary and what fee will be applied. Offer the alternative (if possible)</label>
                                    			</div>
        									</c:if>
        									<c:if test="${empty param.RT1MainCatTool}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-15">
                                        			<label class="custom-control-label" for="checkbox-15">- to ask for the CAT tool tutorials, licenses (if applicable), and additional time to learn how to work with the new CAT tool (if applicable)</label>
                                    			</div>
        									</c:if>
        									<c:if test="${param.Deliveribles == '3' && empty param.LayoutOk}">
        										<div class="custom-control custom-checkbox">
                                        			<input type="checkbox" class="custom-control-input" id="checkbox-16">
                                        			<label class="custom-control-label" for="checkbox-16">to offer the customer to perform OCR, and explain that the source file needs pre-processing</label>
                                    			</div>
        									</c:if>
        								</div>
        								<div class="h6 mt-3">* If your To-Do list is empty - confirm the project request!</div>
        								<div class="h6 mt-3 text-info">At the end of negotiations with the customer:
        									<div class="mx-3">- All instructions should be clear. <br>- Files should be complete, can be opened and are in a processable format. <br>- The service and the workflow should be selected. <br>- The scope of work should be agreed. <br>- The service outcome should be agreed.<br>- The deadline and the budget should be agreed.</div>
        								</div>
   									</c:when>
    								<c:otherwise>
         								<div><i class="fas fa-times-circle"></i> Consult with your Team Lead and write an email to explain why we decline the request</div>
   									</c:otherwise>
								</c:choose>
                       		</c:if>
                            
                            <c:if test="${param.requestType == '123'}">
                                <div><i class="fas fa-check-circle"></i> Check that a guy has an ID</div>
                            </c:if>

                            <c:if test="${not empty param.hasPersonalId}">
                                <div><i class="fas fa-check-circle"></i> Check that a guy has an ID</div>
                            </c:if>

                            <c:if test="${not empty param.hasDrivingLicense}">
                                <div><i class="fas fa-check-circle"></i> Check that a guy has a driving license</div>

                                <c:if test="${not empty param.hasCar or not empty param.hasMotorcycle}">
                                    <h4 class="mb-3 mt-3">Vehicles</h4>
                                </c:if>

                                <c:if test="${not empty param.hasCar}">
                                    <div class="custom-control custom-checkbox">
                                        <input type="checkbox" class="custom-control-input" id="checkbox-93">
                                        <label class="custom-control-label" for="checkbox-93">Check that a guy actually has a car</label>
                                    </div>

                                    <div class="custom-control custom-checkbox">
                                        <input type="checkbox" class="custom-control-input" id="checkbox-94">
                                        <label class="custom-control-label" for="checkbox-94">
                                            <c:choose>
                                                <c:when test="${param.carEngineVolume != '0'}">
                                                    Check that a car actually has a ${param.carEngineVolume} engine
                                                </c:when>
                                                <c:otherwise>
                                                    Check that a car actually has a weak engine
                                                </c:otherwise>
                                            </c:choose>
                                        </label>
                                    </div>
                                </c:if>

                                <c:if test="${not empty param.hasMotorcycle}">
                                    <div class="custom-control custom-checkbox">
                                        <input type="checkbox" class="custom-control-input" id="checkbox-95">
                                        <label class="custom-control-label" for="checkbox-95">Check that a guy actually has a motorcycle</label>
                                    </div>

                                    <c:if test="${not empty param.hasCrazyBike}">
                                        <div class="custom-control custom-checkbox">
                                            <input type="checkbox" class="custom-control-input" id="checkbox-96">
                                            <label class="custom-control-label" for="checkbox-96">Check that a guy is sane</label>
                                        </div>
                                    </c:if>

                                    <div class="custom-control custom-checkbox">
                                        <input type="checkbox" class="custom-control-input" id="checkbox-97">
                                        <label class="custom-control-label" for="checkbox-97">
                                            <c:choose>
                                                <c:when test="${param.bikeEngineVolume != '0'}">
                                                    Check that a motorcycle actually has a ${param.bikeEngineVolume} cc engine
                                                </c:when>
                                                <c:otherwise>
                                                    Check that a motorcycle actually has a weak engine
                                                </c:otherwise>
                                            </c:choose>
                                        </label>
                                    </div>
                                </c:if>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <p class="lead">Nothing to check</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <footer class="my-5 pt-5 text-muted text-center text-small">
                <p class="mb-1">© 2018 Aspect Translation Company</p>
                <ul class="list-inline">
                    <li class="list-inline-item"><a href="#">Privacy</a></li>
                    <li class="list-inline-item"><a href="#">Terms</a></li>
                    <li class="list-inline-item"><a href="#">Support</a></li>
                </ul>
            </footer>
        </div>
        
        <script type="text/javascript">
          $(function () {
            $('[data-toggle="tooltip"]').tooltip();
          })
        </script>
    </body>
</html>
