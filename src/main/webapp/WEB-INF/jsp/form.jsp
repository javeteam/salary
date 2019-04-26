<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Checklist</title>
        <jsp:include page="assets.jsp"/>
    </head>

    <body>
        <form action="form" method="POST">
            <div class="container">
                <div class="py-5 text-center">
                    <h2>Project Request Analysis Checklist</h2>
                </div>

                <div class="row">
                    <div class="col-md-12">
                        <h5 class="my-3">Request Type</h5>
                        <div class="mx-3">
                        	<div class="custom-control custom-radio">
                        		<input type="radio" id="request-type-1" name="requestType" class="custom-control-input" value="0">
                            	<label class="custom-control-label" for="request-type-1">Project email request through TMS</label>
                        	</div>
                        	<div class="custom-control custom-radio">
                            	<input type="radio" id="request-type-2" name="requestType" class="custom-control-input" value="1">
                            	<label class="custom-control-label" for="request-type-2">Personal project email request</label>
                        	</div>
                        </div>
                        
                        <div class="mx-5 collapse" data-show="requestType == '0'">
                            <h5 class="my-2">Language combinations is doable?</h5>
                            <div class="mx-3">
                        		<div class="custom-control custom-radio">
                        			<input type="radio" id="lang-combination-doable-1" name="LangCombinationDoable" class="custom-control-input" value="yes">
                            		<label class="custom-control-label" for="lang-combination-doable-1">Yes</label>
                        		</div>
                        		<div class="custom-control custom-radio">
                            		<input type="radio" id="lang-combination-doable-2" name="LangCombinationDoable" class="custom-control-input" value="no" checked="checked">
                            		<label class="custom-control-label" for="lang-combination-doable-2">No</label>
                        		</div>
                        	</div>
                        	<div class="collapse" data-show="LangCombinationDoable != 'no'">
                            	<h5 class="my-2">Service is doable?</h5>
                            	<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service-doable-1" name="ServiceDoable" class="custom-control-input" value="yes">
                            			<label class="custom-control-label" for="service-doable-1">Yes</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                           				<input type="radio" id="service-doable-2" name="ServiceDoable" class="custom-control-input" value="no" checked="checked">
                           				<label class="custom-control-label" for="service-doable-2">No</label>
                        			</div>
                        		</div>	
                       			<div class="collapse" data-show="ServiceDoable != 'no'">
                           			<h5 class="my-2">Domain is doable?</h5>
                           			<div class="mx-3">
                       					<div class="custom-control custom-radio">
                        					<input type="radio" id="domain-doable-1" name="DomainDoable" class="custom-control-input" value="yes">
                            				<label class="custom-control-label" for="domain-doable-1">Yes</label>
                        				</div>
                        				<div class="custom-control custom-radio">
                            				<input type="radio" id="domain-doable-2" name="DomainDoable" class="custom-control-input" value="no" checked="checked">
                            				<label class="custom-control-label" for="domain-doable-2">No</label>
                        				</div>
                        			</div>
                       			</div>
                        	</div>
                        	
                        	<div class="collapse" data-show="LangCombinationDoable == 'yes' && ServiceDoable == 'yes' && DomainDoable == 'yes'">
                            	<div class="my-2 h5">
                            		<span>Project is in our main CAT tool?</span>
                            		<span data-toggle="tooltip" data-html="true" title="Across, MemoQ, Memsource, Multitrans, SDL Trados Studio, SDL Passolo, Transit, XTM"><i class="fas fa-question-circle"></i></span>
                            	</div>
                            	<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="main-cat-tool-1" name="MainCatTool" class="custom-control-input" value="yes">
                            			<label class="custom-control-label" for="main-cat-tool-1">Yes</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                           				<input type="radio" id="main-cat-tool-2" name="MainCatTool" class="custom-control-input" value="no" checked="checked">
                           				<label class="custom-control-label" for="main-cat-tool-2">No</label>
                        			</div>
                        		</div>
                        		
                        		<h5 class="my-2">Deadline is feasible?</h5>
                        		<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="deadline-feasible-1" name="DeadlineFeasible" class="custom-control-input" value="yes">
                            			<label class="custom-control-label" for="deadline-feasible-1">Yes</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                           				<input type="radio" id="deadline-feasible-2" name="DeadlineFeasible" class="custom-control-input" value="no" checked="checked">
                           				<label class="custom-control-label" for="deadline-feasible-2">No</label>
                        			</div>
                        		</div>
                        	</div>
                        </div>
                        
                        <div class="mx-5 collapse" data-show="requestType == '1'">
                            <h5 class="my-2">
                            	<span>Language Combinations -> Service -> Domain is doable?</span>
                            	<span data-toggle="tooltip" data-html="true" title="Attention! Make sure that the language combination & domain requested corresponds to the ones of the working files!"><i class="fas fa-exclamation-triangle"></i></span>
                            </h5>
                        	<div class="mx-3">
                        		<div class="custom-control custom-radio">
                        			<input type="radio" id="project-doable-1" name="ProjectDoable" class="custom-control-input" value="yes">
                            		<label class="custom-control-label" for="project-doable-1">Yes</label>
                        		</div>
                        		<div class="custom-control custom-radio">
                            		<input type="radio" id="project-doable-2" name="ProjectDoable" class="custom-control-input" value="no" checked="checked">
                            		<label class="custom-control-label" for="project-doable-2">No</label>
                        		</div>
                        	</div>
                        	
                        	<div class="collapse" data-show="ProjectDoable == 'yes'">
                           		<h5 class="my-2">Select the service</h5>
                        		<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service1" name="ServiceType" class="custom-control-input" value="0" checked="checked">
                            			<label class="custom-control-label" for="service1">Translation Only + QA</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="service2" name="ServiceType" class="custom-control-input" value="1">
                            			<label class="custom-control-label" for="service2">Translation + Revision + QA</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service3" name="ServiceType" class="custom-control-input" value="2">
                            			<label class="custom-control-label" for="service3">Translation + Revision + Review + QA</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="service4" name="ServiceType" class="custom-control-input" value="3">
                           	 			<label class="custom-control-label" for="service4">
                            				<span>DTP/OCR + Translation + QA</span>
                            				<span data-toggle="tooltip" data-html="true" title="This service is used when you received<br> - scanned/uneditable pdfs or pictures,<br> - pdfs created in InDesign for marketing purposes,<br> - editable pdfs, docs that contain imbedded elements (tables, pictures, graphics, etc)"><i class="fas fa-question-circle"></i></span>
                            			</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="service5" name="ServiceType" class="custom-control-input" value="4">
                           	 			<label class="custom-control-label" for="service5">
                            				<span>DTP/OCR + Translation + Revision + QA</span>
                            				<span data-toggle="tooltip" data-html="true" title="This service is used when you received<br> - scanned/uneditable pdfs or pictures,<br> - pdfs created in InDesign for marketing purposes,<br> - editable pdfs, docs that contain imbedded elements (tables, pictures, graphics, etc)"><i class="fas fa-question-circle"></i></span>
                            			</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="service6" name="ServiceType" class="custom-control-input" value="5">
                           	 			<label class="custom-control-label" for="service6">
                            				<span>DTP/OCR + Translation + Revision + Review + QA</span>
                            				<span data-toggle="tooltip" data-html="true" title="This service is used when you received<br> - scanned/uneditable pdfs or pictures,<br> - pdfs created in InDesign for marketing purposes,<br> - editable pdfs, docs that contain imbedded elements (tables, pictures, graphics, etc)"><i class="fas fa-question-circle"></i></span>
                            			</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service7" name="ServiceType" class="custom-control-input" value="6">
                            			<label class="custom-control-label" for="service7">LQA</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service8" name="ServiceType" class="custom-control-input" value="7">
                            			<label class="custom-control-label" for="service8">Final Check + QA</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service9" name="ServiceType" class="custom-control-input" value="8">
                            			<label class="custom-control-label" for="service9">Document Alignment</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service10" name="ServiceType" class="custom-control-input" value="9">
                            			<label class="custom-control-label" for="service10">Post-Editing</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service11" name="ServiceType" class="custom-control-input" value="10">
                            			<label class="custom-control-label" for="service11">Term Base Translation</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="service12" name="ServiceType" class="custom-control-input" value="11">
                            			<label class="custom-control-label" for="service12">Term Base Creation</label>
                        			</div>
                        		</div>
                        		
                        		<h5 class="my-2">Is it highly visible text?</h5>
                        		<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="result-usage-1" name="ResultUsage" class="custom-control-input" value="yes">
                            			<label class="custom-control-label" for="result-usage-1">Yes</label>		
                            		</div>
                            		<div class="custom-control custom-radio">
                            			<input type="radio" id="result-usage-2" name="ResultUsage" class="custom-control-input" value="no">
                            			<label class="custom-control-label" for="result-usage-2">No</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="result-usage-3" name="ResultUsage" class="custom-control-input" value="not-clear" checked="checked">
                            			<label class="custom-control-label" for="result-usage-3">Not Clear</label>
                        			</div>
                        		</div>
                        		
                        		<h5 class="my-2">What is the content type?</h5>
                        		<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="content-type-1" name="ContentType" class="custom-control-input" value="0">
                            			<label class="custom-control-label" for="content-type-1">Connected text</label>		
                            		</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="content-type-2" name="ContentType" class="custom-control-input" value="1">
                            			<label class="custom-control-label" for="content-type-2">UI or UA elements</label>
                        			</div>
                        			<div class="collapse" data-show="ContentType == '1'">
                        				<div class="mx-5 custom-control custom-checkbox">
                                   			<input type="checkbox" class="custom-control-input" name="ContentTypeUiUa" id="content-type-ui-ua">
                                   			<label class="custom-control-label" for="content-type-ui-ua">The service is paid per hour</label>
                               			</div>
                               		</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="content-type-3" name="ContentType" class="custom-control-input" value="2">
                            			<label class="custom-control-label" for="content-type-3">List of terms/phrases without the context</label>
                        			</div>
                        			<div class="collapse" data-show="ContentType == '2' && ServiceType != '10'">
                        				<div class="mx-5 custom-control custom-checkbox">
                                   			<input type="checkbox" class="custom-control-input" name="ContentTypeList" id="content-type-list">
                                   			<label class="custom-control-label" for="content-type-list">The service is paid per hour</label>
                               			</div>
                               		</div>
                        		</div>
                        		                        		                       		
                        		<div class="mt-3">
                        			<div class="h5 custom-control custom-checkbox">
                               	    	<input type="checkbox" class="custom-control-input" name="RT1MainCatTool" id="rt1-main-cat-tool">
                                	   	<label class="custom-control-label" for="rt1-main-cat-tool">Project is in our main CAT tool</label>
                               		</div>
                        			<div class="h5 custom-control custom-checkbox">
                                 	  	<input type="checkbox" class="custom-control-input" name="InstrClear" id="instr-clear">
                                 	  	<label class="custom-control-label" for="instr-clear">Instructions are clear</label>
                               		</div>
                               		<div class="h5 custom-control custom-checkbox">
                                  	 	<input type="checkbox" class="custom-control-input" name="RT1DeadlineFeasible" id="rt1-deadline-feasible">
                                 	  	<label class="custom-control-label" for="rt1-deadline-feasible">Deadline is feasible</label>
                               		</div>
                               		<div class="h5 custom-control custom-checkbox">
                                 	  	<input type="checkbox" class="custom-control-input" name="PriceCorrect" id="price-correct">
                                 	  	<label class="custom-control-label" for="price-correct">Price per word/hour corresponds the price of the requested service indicated in TMS</label>
                               		</div>
                               		<div class="h5 custom-control custom-checkbox">
                                  	 	<input type="checkbox" class="custom-control-input" name="CatCorrect" id="cat-correct">
                                   		<label class="custom-control-label" for="cat-correct">The CAT grid corresponds the CAT grid indicated in TMS</label>
                               		</div>
                               		<div class="h5 custom-control custom-checkbox">
                                   		<input type="checkbox" class="custom-control-input" name="StatisticsCorrect" id="statistics-correct">
                                   		<label class="custom-control-label" for="statistics-correct">The CAT tool statistics corresponds the statistic in the project request</label>
                               		</div>
                               		<div class="h5 custom-control custom-checkbox">
                                   		<input type="checkbox" class="custom-control-input" name="MatchesActionsRequired" id="matches-actions-required">
                                   		<label class="custom-control-label" for="matches-actions-required">
                                   		<span>Some actions with Matches/Repetitions are expected</span>
                                   		<span data-toggle="tooltip" data-html="true" title="Matches/Repetitions = CM (101% Match), PM (Exact Match), 100% Match, Repetitions"><i class="fas fa-question-circle"></i></span>
                                   		</label>
                               		</div>
                              	 	<div class="my-2 mx-5 collapse" data-show="MatchesActionsRequired">
                            			<h5>Are they paid?</h5>
                            			<div class="mx-3">
                        					<div class=" custom-control custom-radio">
                        						<input type="radio" id="matches-paid-1" name="MatchesPaid" class="custom-control-input" value="yes">
                            					<label class="custom-control-label" for="matches-paid-1">Yes</label>		
                            				</div>
                        					<div class="custom-control custom-radio">
                            					<input type="radio" id="matches-paid-2" name="MatchesPaid" class="custom-control-input" value="no" checked="checked">
                            					<label class="custom-control-label" for="matches-paid-2">No</label>
                        					</div>
                        				</div>
                        			</div>
                        		</div>
                        		                        		
                        		<h5 class="my-2 mt-3">
                        			<span>All steps of the workflow are paid?</span>
                        			<span data-toggle="tooltip" data-html="true" title="Attention! If the working files require pre- or post-processing, the DTP/OCR service should be added and paid."><i class="fas fa-exclamation-triangle"></i></span>
                        		</h5>
                        		<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="all-steps-paid-1" name="AllStepsPaid" class="custom-control-input" value="yes">
                            			<label class="custom-control-label" for="all-steps-paid-1">Yes</label>		
                            		</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="all-steps-paid-2" name="AllStepsPaid" class="custom-control-input" value="no" checked="checked">
                            			<label class="custom-control-label" for="all-steps-paid-2">No</label>
                        			</div>
                        		</div>
                        		
                        		<h5 class="my-2">Deliveribles</span></h5>
                        		<div class="mx-3">
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="deliveribles-1" name="Deliveribles" class="custom-control-input" value="0">
                            			<label class="custom-control-label" for="deliveribles-1">Bilingual file</label>		
                            		</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="deliveribles-2" name="Deliveribles" class="custom-control-input" value="1">
                            			<label class="custom-control-label" for="deliveribles-2">Return package</label>
                        			</div>
                        			<div class="custom-control custom-radio">
                        				<input type="radio" id="deliveribles-3" name="Deliveribles" class="custom-control-input" value="2">
                            			<label class="custom-control-label" for="deliveribles-3">On-line delivery</label>		
                            		</div>
                        			<div class="custom-control custom-radio">
                            			<input type="radio" id="deliveribles-4" name="Deliveribles" class="custom-control-input" value="3">
                            			<label class="custom-control-label" for="deliveribles-4">Clean file</label>
                        			</div>
                        			<div class="mx-5">
                        				<div class="h6 collapse" data-show="Deliveribles == 3">
                        					<div class="custom-control custom-checkbox">
                                   				<input type="checkbox" class="custom-control-input" name="LayoutOk" id="layout-ok">
                                   				<label class="custom-control-label" for="layout-ok">
                                   					<span>Target layout is OK</span>
                                   					<span data-toggle="tooltip" data-html="true" title="Attention! Perform pseudotranslate in CAT tool and save target as to check the layout"><i class="fas fa-exclamation-triangle"></i></span>
                                   				</label>
                               				</div>
                        				</div>
                        			</div>
                        		</div>
                          	</div>
                        </div>

                        <hr class="mb-4">
                        <button class="btn btn-primary btn-lg btn-block" type="submit">Generate</button>
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
        </form>

        <script type="text/javascript">
          $(function () {
            $('[data-toggle="tooltip"]').tooltip();

            function collectParams() {
              var params = {};

              // Collect params
              $('input').each(function() {
                var $e = $(this);
                var name = $e.prop('name');

                switch ($e.attr('type')) {
                  case 'checkbox':
                    params[name] = $e.is(':checked');
                    break;

                  case 'radio':
                    if ($e.is(':checked')) {
                      params[name] = $e.val();
                    } else {
                      params[name] = params[name] || undefined;
                    }
                    break;
                }
              });

              return params;
            }

            function evaluate(condition, params) {
              with (params) {
                return eval(condition);
              }
            }

            function update() {
              var params = collectParams();

              sections.forEach(function(section) {
                section.$e.collapse(evaluate(section.condition, params) ? 'show' : 'hide');
              });
            }

            var sections = [];
            $('[data-show]').each(function() {
              var $this = $(this);
              sections.push({
                condition: $this.data('show'),
                $e: $this
              });
            });

            $(document).on('change', 'input', function() {
              update();
            });

            update();
          })
        </script>
    </body>
</html>
