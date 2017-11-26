<?php
/* @var $this NapravaController */
/* @var $model Naprava */
/* @var $form CActiveForm */
?>

<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'naprava-form',
	// Please note: When you enable ajax validation, make sure the corresponding
	// controller action is handling ajax validation correctly.
	// There is a call to performAjaxValidation() commented in generated controller code.
	// See class documentation of CActiveForm for details on this.
	'enableAjaxValidation'=>false,
)); ?>

	

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'NAZIV'); ?>
		<?php echo $form->textField($model,'NAZIV',array('size'=>60,'maxlength'=>200)); ?>
		<?php echo $form->error($model,'NAZIV'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'NASLOV'); ?>
		<?php echo $form->textField($model,'NASLOV',array('size'=>60,'maxlength'=>100)); ?>
		<?php echo $form->error($model,'NASLOV'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Kreiraj' : 'Save'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->