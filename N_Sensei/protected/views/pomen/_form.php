<?php
/* @var $this PomenController */
/* @var $model Pomen */
/* @var $form CActiveForm */
?>

<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'pomen-form',
	// Please note: When you enable ajax validation, make sure the corresponding
	// controller action is handling ajax validation correctly.
	// There is a call to performAjaxValidation() commented in generated controller code.
	// See class documentation of CActiveForm for details on this.
	'enableAjaxValidation'=>false,
)); ?>

	

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'ID'); ?>
		<?php echo $form->textField($model,'ID'); ?>
		<?php echo $form->error($model,'ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'SENZOR_ID'); ?>
		<?php echo $form->dropDownList($model, 'SENZOR_ID', CHtml::listData(Senzor::model()->findAll(), 'ID', 'NAZIV')); ?>
		<?php echo $form->error($model,'SENZOR_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'POMEN'); ?>
		<?php echo $form->textField($model,'POMEN',array('size'=>60,'maxlength'=>200)); ?>
		<?php echo $form->error($model,'POMEN'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'MIN_VREDNOST'); ?>
		<?php echo $form->textField($model,'MIN_VREDNOST',array('size'=>60,'maxlength'=>100)); ?>
		<?php echo $form->error($model,'MIN_VREDNOST'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'MAX_VREDNOST'); ?>
		<?php echo $form->textField($model,'MAX_VREDNOST',array('size'=>60,'maxlength'=>100)); ?>
		<?php echo $form->error($model,'MAX_VREDNOST'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Kreiraj' : 'Save'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->